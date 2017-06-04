package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import java.io.IOException;

public final class UnknownFilePreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) throws IOException {
        throw new UnsupportedOperationException(); //TODO implement
    }
}
