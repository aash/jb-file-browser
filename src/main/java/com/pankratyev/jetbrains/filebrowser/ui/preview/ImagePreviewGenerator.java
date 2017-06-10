package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link PreviewGenerator} implementation for image files (bmp, png, gif, jpeg).
 */
public final class ImagePreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) throws IOException {
        try (InputStream is = fileObject.getInputStream()) {
            if (is == null) {
                //FIXME what if file was deleted? Show some "absent file" preview?
                // this implementation shouldn't be used with such FileObject
                throw new RuntimeException("Cannot read file contents: " + fileObject);
            }
            BufferedImage fileImage = ImageIO.read(is);
            Image previewImage = ImageScaleUtils.resizeIfNecessary(fileImage, maxWidth, maxHeight);
            return new JLabel(new ImageIcon(previewImage));
        }
    }
}
