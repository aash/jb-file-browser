package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowser;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public final class FileListDoubleClickListener extends MouseAdapter {
    private final Logger LOGGER = LoggerFactory.getLogger(FileListDoubleClickListener.class);

    private final FileBrowser browser;

    public FileListDoubleClickListener(FileBrowser browser) {
        this.browser = browser;
    }

    @SuppressWarnings("unchecked") // this listener is to be used with JList<FileObject>
    @Override
    public void mouseClicked(MouseEvent e) {
        //TODO consider using consume()  or  getClickCount() % 2 == 0
        if (e.getClickCount() == 2) {
            JList<FileObject> fileList = (JList<FileObject>) e.getSource();
            int index = fileList.locationToIndex(e.getPoint());
            FileObject selectedFileObject = fileList.getModel().getElementAt(index);

            //TODO move it to separate place (Controller class that will handle navigation and displaying preview)
            if (selectedFileObject.isDirectory()) {
                try {
                    browser.setCurrentDirectory(selectedFileObject);
                } catch (IOException ex) {
                    //TODO handle it more properly
                    LOGGER.error("Unable to change directory", ex);
                }
            }
        }
    }
}
