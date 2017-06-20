package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.ftp.FtpClient;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.FileType;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.provider.FileTypeProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;
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

    private volatile FileObject currentFileObject = null;

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

        if (!fileObject.isDirectory() && !ZipUtils.isZipArchive(fileObject)) {
            return;
        }

        // changing the directory may take some time (e.g. when using FTP); so it should be executed in separate thread
        runSwingWorker(new SwingWorker<List<FileObject>, Void>() {
            @Override
            protected void process(List<Void> chunks) {
                if (browser.isFtpMode()) {
                    browser.showPreloader();
                }
            }

            @Override
            protected List<FileObject> doInBackground() throws IOException {
                publish();

                List<FileObject> fileObjectsToDisplay = new ArrayList<>();

                // add '..' parent folder
                if (fileObject.hasParent()) {
                    fileObjectsToDisplay.add(ParentDirFileObject.createFor(fileObject));
                }

                List<FileObject> children = fileObject.getChildren();
                if (children != null) {
                    fileObjectsToDisplay.addAll(children);
                } else {
                    // archive in archive
                    return null;
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
                    if (fileObjectsToDisplay != null) {
                        browser.setCurrentDirectoryContents(fileObjectsToDisplay);
                        browser.clearPreview();
                        browser.setCurrentPath(fileObject.getFullName());

                        // select previously opened child if was navigated to parent
                        if (currentFileObject != null) { // null on initial folder display
                            browser.setSelectedFileObject(currentFileObject);
                        }

                        currentFileObject = fileObject;
                        LOGGER.debug("cd: {}", fileObject);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof AccessDeniedException) {
                        browser.showErrorDialog("Access denied");
                    } else {
                        browser.showErrorDialog("An error occurred: " + e.getMessage());
                        LOGGER.error(null, e);
                    }
                    browser.clearPreview();
                }
            }
        });
    }

    /**
     * Handles file list element selection. Updates preview panel.
     * Preloader is displayed while preview is being generated.
     * @param fileObject selected element in file list.
     */
    public void showPreview(@Nonnull final FileObject fileObject) {
        showPreview(fileObject, true);
    }

    /**
     * Handles file list element selection. Updates preview panel.
     * @param fileObject selected element in file list.
     * @param showPreloader whether the preloader should be displayed while preview is being generated.
     */
    void showPreview(@Nonnull final FileObject fileObject, final boolean showPreloader) {
        ensureEdt();

        runSwingWorker(new SwingWorker<JComponent, Void>() {
            @Override
            protected void process(List<Void> chunks) {
                if (showPreloader) {
                    browser.showPreloader();
                }
            }

            @Override
            protected JComponent doInBackground() throws IOException {
                publish();

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
            private volatile String absolutePathToDisplay = null;

            @Override
            protected void process(List<Void> chunks) {
                browser.showPreloader();
            }

            @Override
            protected List<FileObject> doInBackground() {
                publish();

                try {
                    FileObject fileObject = client.getInitialDirectory();
                    LOGGER.debug("Connected to FTP: {}", fileObject);
                    absolutePathToDisplay = fileObject.getFullName();

                    List<FileObject> ftpContents = fileObject.getChildren();
                    List<FileObject> fileObjectsToDisplay = new ArrayList<>();

                    if (fileObject.hasParent()) {
                        fileObjectsToDisplay.add(ParentDirFileObject.createFor(fileObject));
                    }
                    if (ftpContents != null) {
                        fileObjectsToDisplay.addAll(ftpContents);
                        currentFileObject = fileObject;
                    } else {
                        // shouldn't happen
                        LOGGER.error("Unexpected null children for FTP initial file object: " + fileObject);
                    }

                    return fileObjectsToDisplay;
                } catch (IOException e) {
                    LOGGER.warn("An error occurred while displaying FTP server contents", e);
                    browser.showErrorDialog(
                            "An error occurred while displaying FTP server contents: " + e.getMessage());
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
                        browser.enableFtpMode(client.getFtpUrl());
                        if (absolutePathToDisplay != null) {
                            browser.setCurrentPath(absolutePathToDisplay);
                        } else {
                            // shouldn't happen
                            LOGGER.error("Unexpected null path to display");
                        }
                    } else {
                        backToInitialDirectory();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    LOGGER.error(null, e);
                    browser.showErrorDialog("An error occurred: " + e.getMessage());
                    backToInitialDirectory();
                }
            }

            private void backToInitialDirectory() {
                // go back to initial local directory if for some reason FTP contents are not shown
                LOGGER.warn("Cannot show FTP contents, going back to initial local directory");
                disconnectFromFtp();
                changeDirectoryToInitial();
                browser.clearPreview();
            }
        });
    }

    public void disconnectFromFtp() {
        ensureEdt();
        ftpClient = null;
        browser.disableFtpMode();
    }

    /**
     * Reopens currently opened path (directory or archive). If FTP connection is established a local copies
     * for currently opened path will be invalidated.
     */
    public void refresh() {
        ensureEdt();

        if (currentFileObject == null) {
            // shouldn't happen
            LOGGER.error("Unexpected state: no current file object");
            return;
        }

        if (ftpClient != null) {
            ftpClient.getLocalCopyManager().invalidate(currentFileObject.getFullName());
        }

        changeDirectory(currentFileObject);
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
