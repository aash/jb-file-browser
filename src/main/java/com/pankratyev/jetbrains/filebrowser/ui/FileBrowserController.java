package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the logic of handling user actions in file browser.
 * This and only this class should be used to change {@link FileBrowser} UI state.
 */
public final class FileBrowserController {
    private final Logger LOGGER = LoggerFactory.getLogger(FileBrowserController.class);

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
    public void changeDirectory(FileObject fileObject) {
        ensureEdt();

        if (!fileObject.isDirectory()) {
            return;
        }

        //TODO this should be done in separate thread (not in EDT)
        List<FileObject> fileObjectsToDisplay = new ArrayList<>();

        // add '..' parent folder
        FileObject parent = fileObject.getParent();
        if (parent != null) {
            fileObjectsToDisplay.add(ParentDirFileObject.wrap(parent));
        }

        try {
            Collection<FileObject> children = fileObject.getChildren();
            if (children != null) {
                fileObjectsToDisplay.addAll(children);
            } else {
                // shouldn't happen since isDirectory() is true
                LOGGER.error("Unexpected null children for file object: " + fileObject);
            }

            browser.setCurrentDirectoryContents(fileObjectsToDisplay);
            browser.clearPreview();
        } catch (IOException e) {
            //TODO handle it more properly (display some error message)
            LOGGER.error("Unable to change directory", e);
        }
    }

    /**
     * Handles file list element selection. Updates preview panel.
     * @param fileObject selected element in file list.
     */
    public void showPreview(FileObject fileObject) {
        ensureEdt();

        FileType type = fileTypeProvider.getType(fileObject);

        Dimension previewPanelSize = browser.getPreviewPanelSize();
        int previewMaxWidth = (int) previewPanelSize.getWidth();
        int previewMaxHeight = (int) previewPanelSize.getHeight();

        //TODO this MUST NOT be executed in UI thread since reading file content may take a long time
        try {
            JComponent previewComponent = type.getPreviewGenerator()
                    .generatePreview(fileObject, previewMaxWidth, previewMaxHeight);
            browser.setPreview(previewComponent);
        } catch (IOException e) {
            //TODO handle it more properly (display some error message)
            LOGGER.error("Unable to change directory", e);
        }
    }

    private static void ensureEdt() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException(Thread.currentThread().getName());
        }
    }
}
