package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
     * TODO tasks like rendering a preview and changing the directory could be run in parallel; this would improve UX
     */
    private final AtomicReference<SwingWorker<?, ?>> runningWorker = new AtomicReference<>();

    private final FileBrowser browser;
    private final FileTypeProvider fileTypeProvider;

    FileBrowserController(FileBrowser browser, FileTypeProvider fileTypeProvider) {
        this.browser = Objects.requireNonNull(browser);
        this.fileTypeProvider = Objects.requireNonNull(fileTypeProvider);
    }

    /**
     * Changes current directory. It causes clearing currently displayed preview.
     * Should be used to handle action performed on file list element (double click or Enter key press).
     * @param fileObject element in file list; if it is not a directory no actions will be performed.
     */
    public void changeDirectory(final FileObject fileObject) {
        ensureEdt();

        if (!fileObject.isDirectory()) {
            return;
        }

        // changing the directory may take some time (e.g. when using FTP); so it should be executed in separate thread
        runSwingWorker(new SwingWorker<List<FileObject>, Void>() {
            @Override
            protected List<FileObject> doInBackground() throws IOException {
                List<FileObject> fileObjectsToDisplay = new ArrayList<>();

                // add '..' parent folder
                FileObject parent = fileObject.getParent();
                if (parent != null) {
                    fileObjectsToDisplay.add(ParentDirFileObject.wrap(parent));
                }

                Collection<FileObject> children = fileObject.getChildren();
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
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    //TODO handle it more properly (display some error message)
                    LOGGER.error(null, e);
                }
            }
        });
    }

    /**
     * Handles file list element selection. Updates preview panel.
     * @param fileObject selected element in file list.
     */
    public void showPreview(final FileObject fileObject) {
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
                    //TODO handle it more properly (display some error message)
                    LOGGER.error(null, e);
                }
            }
        });
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
