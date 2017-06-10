package com.pankratyev.jetbrains.filebrowser.ui.userdir;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObjectFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

final class UserDirectoryLinkMouseListener extends MouseAdapter {
    private final FileBrowserController controller;

    UserDirectoryLinkMouseListener(FileBrowserController controller) {
        this.controller = controller;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        UserDirectoryLink link = (UserDirectoryLink) e.getSource();
        String userDirAbsolutePath = link.getUserDirPath();
        LocalFileObject fileObject = LocalFileObjectFactory.create(userDirAbsolutePath);
        controller.changeDirectory(fileObject);
    }
}
