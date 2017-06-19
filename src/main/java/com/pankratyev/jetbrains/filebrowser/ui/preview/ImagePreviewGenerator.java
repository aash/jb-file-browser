package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedByInterruptException;

/**
 * {@link PreviewGenerator} implementation for image files (bmp, png, gif, jpeg).
 */
public final class ImagePreviewGenerator implements PreviewGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewGenerator.class);

    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) {
        try {
            try (InputStream is = fileObject.getInputStream()) {
                if (is != null) {
                    BufferedImage fileImage = ImageIO.read(is);
                    if (fileImage != null) {
                        return PreviewUtils.getPreviewComponent(fileImage, maxWidth, maxHeight);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Cannot generate a preview for an image: " + fileObject, e);
        }

        return PreviewUtils.getBrokenFilePreview(maxWidth, maxHeight);
    }
}
