package com.pankratyev.jetbrains.filebrowser.ui.userdir;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObjectFactory;

import javax.annotation.Nonnull;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

final class UserDirectoryLinkMouseListener extends MouseAdapter {
    private final FileBrowserController controller;

    UserDirectoryLinkMouseListener(@Nonnull FileBrowserController controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        UserDirectoryLink link = (UserDirectoryLink) e.getSource();
        String userDirAbsolutePath = link.getUserDirPath();
        LocalFileObject fileObject = LocalFileObjectFactory.create(userDirAbsolutePath);
        controller.changeDirectory(fileObject);
    }
}
