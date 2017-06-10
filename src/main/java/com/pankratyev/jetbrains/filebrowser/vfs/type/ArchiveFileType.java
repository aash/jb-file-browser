package com.pankratyev.jetbrains.filebrowser.vfs.type;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.ui.preview.ArchivePreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;

import javax.annotation.Nonnull;
import javax.swing.Icon;

public final class ArchiveFileType implements FileType {
    @Nonnull
    @Override
    public Icon getIcon() {
        return IconRegistry.ARCHIVE;
    }

    @Nonnull
    @Override
    public PreviewGenerator getPreviewGenerator() {
        return new ArchivePreviewGenerator();
    }
}
