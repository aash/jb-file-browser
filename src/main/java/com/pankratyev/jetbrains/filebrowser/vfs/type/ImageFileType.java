package com.pankratyev.jetbrains.filebrowser.vfs.type;

import javax.annotation.Nonnull;
import javax.swing.Icon;

public abstract class ImageFileType implements FileType {
    @Nonnull
    @Override
    public Icon getIcon() {
        throw new UnsupportedOperationException(); //TODO implement
    }
}
