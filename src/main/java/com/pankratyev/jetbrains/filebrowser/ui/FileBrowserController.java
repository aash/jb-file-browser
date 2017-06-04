package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Encapsulates the logic of handling user actions in file browser.
 * This and only this class should be used to change {@link FileBrowser} UI state.
 */
public final class FileBrowserController {
    private final Logger LOGGER = LoggerFactory.getLogger(FileBrowserController.class);

    private final FileBrowser browser;

    public FileBrowserController(FileBrowser browser) {
        this.browser = browser;
    }

    /**
     * Changes current directory. It causes clearing currently displayed preview.
     * Should be used to handle action performed on file list element (double click or Enter key press).
     * @param fileObject element in file list; if it is not a directory no actions will be performed.
     */
    public void changeDirectory(FileObject fileObject) {
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
}
