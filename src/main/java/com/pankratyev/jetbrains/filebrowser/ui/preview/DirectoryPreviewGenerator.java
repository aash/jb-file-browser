package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

public final class DirectoryPreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public BufferedImage generatePreview(FileObject fileObject) {
        return IconRegistry.FOLDER_PREVIEW;
    }
}
