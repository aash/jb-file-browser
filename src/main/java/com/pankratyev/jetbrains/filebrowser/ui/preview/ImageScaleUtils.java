package com.pankratyev.jetbrains.filebrowser.ui.preview;

import java.awt.Image;
import java.awt.image.BufferedImage;

final class ImageScaleUtils {
    private ImageScaleUtils() {
    }

    static Image resizeIfNecessary(BufferedImage image, int maxWidth, int maxHeight) {
        int previewWidth = image.getWidth();
        int previewHeight = image.getHeight();

        Image resultImage = image;
        if (previewWidth > maxWidth || previewHeight > maxHeight) {
            //TODO save width/height ratio
            resultImage = image.getScaledInstance(maxWidth, maxHeight, Image.SCALE_DEFAULT);
        }
        return resultImage;
    }
}
