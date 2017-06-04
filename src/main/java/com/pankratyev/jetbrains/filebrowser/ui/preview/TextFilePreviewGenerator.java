package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

public final class TextFilePreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public BufferedImage generatePreview(FileObject fileObject) {
        throw new UnsupportedOperationException(); //TODO implement
    }
}
