package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Encapsulates the logic of handling user actions in file browser.
 */
public final class FileBrowserController {
    private final Logger LOGGER = LoggerFactory.getLogger(FileBrowserController.class);

    private final FileBrowser browser;

    public FileBrowserController(FileBrowser browser) {
        this.browser = browser;
    }

    /**
     * Changes current directory. It causes clearing displayed preview
     * @param fileObject directory to switch to; if it is not a directory no actions will be performed.
     */
    public void changeDirectory(FileObject fileObject) {
        if (!fileObject.isDirectory()) {
            return;
        }

        //TODO this should be done in separate thread (not in EDT)
        try {
            browser.setCurrentDirectory(fileObject);
            browser.clearPreview();
        } catch (IOException e) {
            //TODO handle it more properly
            LOGGER.error("Unable to change directory", e);
        }
    }
}
