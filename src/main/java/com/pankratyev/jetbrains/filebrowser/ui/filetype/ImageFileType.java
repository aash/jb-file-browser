package com.pankratyev.jetbrains.filebrowser.ui.filetype;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.ui.preview.ImagePreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;

import javax.annotation.Nonnull;
import javax.swing.Icon;

public final class ImageFileType implements FileType {
    @Nonnull
    @Override
    public Icon getIcon() {
        return IconRegistry.IMAGE;
    }

    @Nonnull
    @Override
    public PreviewGenerator getPreviewGenerator() {
        return new ImagePreviewGenerator();
    }
}
