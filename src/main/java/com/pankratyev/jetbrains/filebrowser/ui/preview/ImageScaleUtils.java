package com.pankratyev.jetbrains.filebrowser.ui.preview;

import java.awt.Image;
import java.awt.image.BufferedImage;

final class ImageScaleUtils {
    private ImageScaleUtils() {
    }

    /**
     * Resizes an image if it's width and/or height is greater than the maximal one. Aspect ratio is saved.
     */
    static Image resizeIfNecessary(BufferedImage image, int maxWidth, int maxHeight) {
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
