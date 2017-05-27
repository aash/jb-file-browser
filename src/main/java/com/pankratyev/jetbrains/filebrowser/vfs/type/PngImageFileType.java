package com.pankratyev.jetbrains.filebrowser.vfs.type;

import com.pankratyev.jetbrains.filebrowser.ui.preview.PngImagePreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;

import javax.annotation.Nonnull;

public final class PngImageFileType extends ImageFileType {
    @Nonnull
    @Override
    public PreviewGenerator getPreviewGenerator() {
        return new PngImagePreviewGenerator();
    }
}
