package com.pankratyev.jetbrains.filebrowser.vfs.type;

import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.ui.preview.TextFilePreviewGenerator;

import javax.annotation.Nonnull;
import javax.swing.Icon;

public final class TextFileType implements FileType {
    @Nonnull
    @Override
    public Icon getIcon() {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Nonnull
    @Override
    public PreviewGenerator getPreviewGenerator() {
        return new TextFilePreviewGenerator();
    }
}
