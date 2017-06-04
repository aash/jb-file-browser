package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Image;

public final class DirectoryPreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) {
        Image previewImage = ImageScaleUtils.resizeIfNecessary(IconRegistry.FOLDER_PREVIEW, maxWidth, maxHeight);
        return new JLabel(new ImageIcon(previewImage));
    }
}
