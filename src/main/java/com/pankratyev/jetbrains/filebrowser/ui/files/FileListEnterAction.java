package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.util.Objects;

public final class FileListEnterAction extends AbstractAction {
    private final FileBrowserController controller;

    public FileListEnterAction(@Nonnull FileBrowserController controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    @SuppressWarnings("unchecked") // this action is to be used with JList<FileObject>
    @Override
    public void actionPerformed(ActionEvent e) {
        JList<FileObject> fileList = (JList<FileObject>) e.getSource();
        FileObject selectedFileObject = fileList.getSelectedValue();
        if (selectedFileObject != null) {
            controller.changeDirectory(selectedFileObject);
        }
    }
}
