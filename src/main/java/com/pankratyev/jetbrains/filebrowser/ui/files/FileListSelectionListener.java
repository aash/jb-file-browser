package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Objects;

public final class FileListSelectionListener implements ListSelectionListener {
    private final FileBrowserController controller;

    public FileListSelectionListener(@Nonnull FileBrowserController controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    @SuppressWarnings("unchecked") // this listener is to be used with JList<FileObject>
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            JList<FileObject> fileList = (JList<FileObject>) e.getSource();
            int index = fileList.getSelectedIndex();
            if (index < 0) {
                return;
            }
            //TODO for FtpFileObjects show preview with delay if element is still selected to avoid unnecessary requests to FTP
            FileObject selectedFileObject = fileList.getModel().getElementAt(index);
            controller.showPreview(selectedFileObject);
        }
    }
}
