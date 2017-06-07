package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public final class FileListDoubleClickListener extends MouseAdapter {
    private final FileBrowserController controller;

    public FileListDoubleClickListener(@Nonnull FileBrowserController controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    @SuppressWarnings("unchecked") // this listener is to be used with JList<FileObject>
    @Override
    public void mouseClicked(MouseEvent e) {
        // '% 2 == 0' instead of '== 2' to avoid not working listener
        // if cursor position is not changed between two double-clicks
        if (e.getClickCount() % 2 == 0) {
            JList<FileObject> fileList = (JList<FileObject>) e.getSource();
            int index = fileList.locationToIndex(e.getPoint());
            if (index < 0) {
                return;
            }
            FileObject selectedFileObject = fileList.getModel().getElementAt(index);
            controller.changeDirectory(selectedFileObject);
        }
    }
}
