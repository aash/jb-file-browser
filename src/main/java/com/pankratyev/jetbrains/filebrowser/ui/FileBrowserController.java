package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.ftp.FtpClient;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Encapsulates the logic of handling user actions in file browser.
 * This and only this class should be used to change {@link FileBrowser} UI state.
 */
public final class FileBrowserController {
    private final Logger LOGGER = LoggerFactory.getLogger(FileBrowserController.class);

    /**
     * UI-related task currently being executed. Only one task can be running at once to avoid performance issues.
     */
    private final AtomicReference<SwingWorker<?, ?>> runningWorker = new AtomicReference<>();

    private final FileBrowser browser;
    private final FileTypeProvider fileTypeProvider;
    private final FileObject initialFileObject;

    private FtpClient ftpClient = null;

    FileBrowserController(@Nonnull FileBrowser browser, @Nonnull FileTypeProvider fileTypeProvider,
            @Nonnull FileObject initialFileObject) {
        this.browser = Objects.requireNonNull(browser);
        this.fileTypeProvider = Objects.requireNonNull(fileTypeProvider);
        this.initialFileObject = Objects.requireNonNull(initialFileObject);
    }

    /**
     * Changes current directory to the initial one. It causes clearing currently displayed preview.
     */
    public void changeDirectoryToInitial() {
        changeDirectory(initialFileObject);
    }

    /**
     * Changes current directory. It causes clearing currently displayed preview.
     * Should be used to handle action performed on file list element (double click or Enter key press).
     * @param fileObject element in file list; if it is not a directory and not an archive no actions will be performed.
     */
    public void changeDirectory(@Nonnull final FileObject fileObject) {
        ensureEdt();

        if (!fileObject.isDirectory() && !fileObject.isZipArchive()) {
            return;
        }

        // changing the directory may take some time (e.g. when using FTP); so it should be executed in separate thread
        runSwingWorker(new SwingWorker<List<FileObject>, Void>() {
            @Override
            protected List<FileObject> doInBackground() throws IOException {
                List<FileObject> fileObjectsToDisplay = new ArrayList<>();

                // add '..' parent folder
                if (fileObject.hasParent()) {
                    fileObjectsToDisplay.add(ParentDirFileObject.createFor(fileObject));
                }

                List<FileObject> children = fileObject.getChildren();
                if (children != null) {
                    fileObjectsToDisplay.addAll(children);
                } else {
                    // shouldn't happen since isDirectory() is true
                    LOGGER.error("Unexpected null children for file object: " + fileObject);
                }

                return fileObjectsToDisplay;
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    return;
                }

                try {
                    List<FileObject> fileObjectsToDisplay = get();
                    browser.setCurrentDirectoryContents(fileObjectsToDisplay);
                    browser.clearPreview();
                    browser.setCurrentPath(fileObject.getFullName());
                    LOGGER.debug("cd: {}", fileObject);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof AccessDeniedException) {
                        browser.showErrorDialog("Access denied");
                    } else {
                        browser.showErrorDialog("An error occurred: " + e.getMessage());
                        LOGGER.error(null, e);
                    }
                }
            }
        });
    }

    /**
     * Handles file list element selection. Updates preview panel.
     * @param fileObject selected element in file list.
     */
    public void showPreview(@Nonnull final FileObject fileObject) {
        ensureEdt();

        runSwingWorker(new SwingWorker<JComponent, Void>() {
            @Override
            protected JComponent doInBackground() throws IOException {
                FileType type = fileTypeProvider.getType(fileObject);

                Dimension previewPanelSize = browser.getPreviewPanelSize();
                int previewMaxWidth = (int) previewPanelSize.getWidth();
                int previewMaxHeight = (int) previewPanelSize.getHeight();

                return type.getPreviewGenerator()
                        .generatePreview(fileObject, previewMaxWidth, previewMaxHeight);
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    return;
                }

                try {
                    JComponent previewComponent = get();
                    browser.setPreview(previewComponent);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    browser.showErrorDialog("An error occurred: " + e.getMessage());
                    LOGGER.error(null, e);
                }
            }
        });
    }

    /**
     * Switches application to FTP mode and shows FTP server contents in file list.
     * @param client initialized FTP client with established connection.
     */
    public void connectToFtp(@Nonnull final FtpClient client) {
        ensureEdt();
        this.ftpClient = client;

        runSwingWorker(new SwingWorker<List<FileObject>, Void>() {
            private String absolutePathToDisplay = null;

            @Override
            protected List<FileObject> doInBackground() throws Exception {
                try {
                    FileObject fileObject = client.getCurrentDirectory();
                    LOGGER.debug("Connected to FTP: {}", fileObject);
                    absolutePathToDisplay = fileObject.getFullName();

                    List<FileObject> ftpContents = fileObject.getChildren();
                    List<FileObject> fileObjectsToDisplay = new ArrayList<>();

                    if (fileObject.hasParent()) {
                        fileObjectsToDisplay.add(ParentDirFileObject.createFor(fileObject));
                    }
                    if (ftpContents != null) {
                        fileObjectsToDisplay.addAll(ftpContents);
                    } else {
                        // shouldn't happen
                        LOGGER.error("Unexpected null children for FTP initial file object: " + fileObject);
                    }

                    return fileObjectsToDisplay;
                } catch (IOException e) {
                    LOGGER.warn("An error occurred while displaying FTP server contents", e);
                    browser.showErrorDialog(
                            "An error occurred while displaying FTP server contents: " + e.getMessage());
                    disconnectFromFtp();
                    return null;
                }
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    backToInitialDirectory();
                    return;
                }

                try {
                    List<FileObject> fileObjectsToDisplay = get();
                    if (fileObjectsToDisplay != null) {
                        browser.setCurrentDirectoryContents(fileObjectsToDisplay);
                        browser.clearPreview();
                        if (absolutePathToDisplay != null) {
                            browser.setCurrentPath(absolutePathToDisplay);
                        } else {
                            // shouldn't happen
                            LOGGER.error("Unexpected null path to display");
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    LOGGER.error(null, e);
                    browser.showErrorDialog("An error occurred: " + e.getMessage());
                    backToInitialDirectory();
                }
            }

            //TODO better go to the previous directory
            private void backToInitialDirectory() {
                // go back to initial local directory if for some reason FTP contents are not shown
                LOGGER.warn("Cannot show FTP contents, going back to initial local directory");
                disconnectFromFtp();
                FileBrowserController.this.changeDirectoryToInitial();
            }
        });
    }

    public void disconnectFromFtp() {
        ensureEdt();

        if (ftpClient != null) {
            ftpClient.disconnect();
            ftpClient = null;
        }
    }

    private void runSwingWorker(SwingWorker<?, ?> newWorker) {
        SwingWorker<?, ?> oldWorker = runningWorker.getAndSet(newWorker);
        if (oldWorker != null) {
            oldWorker.cancel(true);
        }
        newWorker.execute();
    }

    private static void ensureEdt() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException(Thread.currentThread().getName());
        }
    }
}
