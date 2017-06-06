package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Image;
import java.io.IOException;

public final class UnknownFilePreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) throws IOException {
        Image previewImage = ImageScaleUtils.resizeIfNecessary(IconRegistry.PLAIN_FILE_PREVIEW, maxWidth, maxHeight);
        return new JLabel(new ImageIcon(previewImage));
    }
}
