package com.pankratyev.jetbrains.filebrowser.ui.userdir;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObjectFactory;

import javax.annotation.Nonnull;
import javax.swing.JLabel;
import java.awt.Cursor;
import java.util.Objects;

/**
 * Link to user directory. Holds an absolute path that can be used to obtain {@link LocalFileObject} instance via
 * {@link LocalFileObjectFactory}.
 */
public final class UserDirectoryLink extends JLabel {
    private final String userDirPath;

    public UserDirectoryLink(@Nonnull String userDirPath, @Nonnull FileBrowserController controller) {
        super(getLabel(Objects.requireNonNull(userDirPath)));
        this.userDirPath = userDirPath;
        setToolTipText(userDirPath);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new UserDirectoryLinkMouseListener(controller));
    }

    String getUserDirPath() {
        return userDirPath;
    }

    private static final String getLabel(String absolutePath) {
        String name = VfsUtils.getNameFromAbsolutePath(absolutePath);
        return "<HTML><U>" + name + "</U></HTML>";
    }
}