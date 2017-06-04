package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.JLabel;

public final class DirectoryPreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject) {
        return new JLabel(IconRegistry.FOLDER_PREVIEW);
    }
}
