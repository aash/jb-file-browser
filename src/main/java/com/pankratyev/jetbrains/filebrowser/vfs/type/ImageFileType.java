package com.pankratyev.jetbrains.filebrowser.vfs.type;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;

import javax.annotation.Nonnull;
import javax.swing.Icon;

public abstract class ImageFileType implements FileType {
    @Nonnull
    @Override
    public Icon getIcon() {
        return IconRegistry.IMAGE;
    }
}
