package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link PreviewGenerator} implementation for image files (bmp, png, gif, jpeg).
 */
public final class ImagePreviewGenerator implements PreviewGenerator {
    @Nonnull
    @Override
    public BufferedImage generatePreview(FileObject fileObject) throws IOException {
        try (InputStream is = fileObject.getInputStream()) {
            if (is == null) {
                // this implementation shouldn't be used with such FileObject
                throw new RuntimeException("Cannot read file contents: " + fileObject);
            }
            return ImageIO.read(is);
        }
    }
}
