package com.pankratyev.jetbrains.filebrowser.vfs.type;

import com.pankratyev.jetbrains.filebrowser.ui.preview.JpegImagePreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;

import javax.annotation.Nonnull;

public final class JpegImageFileType extends ImageFileType {
    @Nonnull
    @Override
    public PreviewGenerator getPreviewGenerator() {
        return new JpegImagePreviewGenerator();
    }
}
