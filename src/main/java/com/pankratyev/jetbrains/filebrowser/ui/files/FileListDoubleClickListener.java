package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class FileListDoubleClickListener extends MouseAdapter {
    private final FileBrowserController controller;

    public FileListDoubleClickListener(FileBrowserController controller) {
        this.controller = controller;
    }

    @SuppressWarnings("unchecked") // this listener is to be used with JList<FileObject>
    @Override
    public void mouseClicked(MouseEvent e) {
        //TODO consider using consume()  or  getClickCount() % 2 == 0
        if (e.getClickCount() == 2) {
            JList<FileObject> fileList = (JList<FileObject>) e.getSource();
            int index = fileList.locationToIndex(e.getPoint());
            FileObject selectedFileObject = fileList.getModel().getElementAt(index);
            controller.changeDirectory(selectedFileObject);
        }
    }
}
