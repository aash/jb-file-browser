package com.pankratyev.jetbrains.filebrowser.ui.filetype;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.ui.preview.UnknownFilePreviewGenerator;

import javax.annotation.Nonnull;
import javax.swing.Icon;

public final class UnknownFileType implements FileType {
    @Nonnull
    @Override
    public Icon getIcon() {
        return IconRegistry.PLAIN_FILE;
    }

    @Nonnull
    @Override
    public PreviewGenerator getPreviewGenerator() {
        return new UnknownFilePreviewGenerator();
    }
}
