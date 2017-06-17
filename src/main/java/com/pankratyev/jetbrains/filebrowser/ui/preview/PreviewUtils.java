package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.ui.IconRegistry;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;

final class PreviewUtils {
    private PreviewUtils() {
    }

    static JComponent getBrokenFilePreview(int maxWidth, int maxHeight) {
        return getPreviewComponent(IconRegistry.BROKEN_PREVIEW, maxWidth, maxHeight);
    }

    static JComponent getPreviewComponent(BufferedImage previewImage, int maxWidth, int maxHeight) {
        Image resizedImage = resizeIfNecessary(previewImage, maxWidth, maxHeight);
        return new JLabel(new ImageIcon(resizedImage));
    }

    /**
     * Resizes an image if it's width and/or height is greater than the maximal one. Aspect ratio is saved.
     */
    private static Image resizeIfNecessary(BufferedImage image, int maxWidth, int maxHeight) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        Image resultImage = image;
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (originalWidth > maxWidth) {
                newWidth = maxWidth;
                newHeight = (newWidth * originalHeight) / originalWidth;
            }

            if (newHeight > maxHeight) {
                newHeight = maxHeight;
                newWidth = (newHeight * originalWidth) / originalHeight;
            }

            resultImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
        }

        return resultImage;
    }
}
