package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JComponent;

public final class DirectoryPreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public JComponent generatePreview(@Nonnull FileObject fileObject, int maxWidth, int maxHeight) {
        return PreviewUtils.getPreviewComponent(IconRegistry.FOLDER_PREVIEW, maxWidth, maxHeight);
    }
}
