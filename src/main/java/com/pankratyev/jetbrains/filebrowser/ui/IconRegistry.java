package com.pankratyev.jetbrains.filebrowser.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public final class IconRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(IconRegistry.class);

    private IconRegistry() {
    }


    public static final ImageIcon FOLDER = getIcon("folder.png");
    public static final ImageIcon IMAGE = getIcon("image.png");
    public static final ImageIcon TEXT_FILE = getIcon("text-file.png");
    public static final ImageIcon PLAIN_FILE = getIcon("plain-file.png");

    public static final BufferedImage FOLDER_PREVIEW = getImage("folder-preview.png");
    public static final BufferedImage PLAIN_FILE_PREVIEW = getImage("plain-file-preview.png");


    private static ImageIcon getIcon(String name) {
        URL resourceUrl = getResourceUrl(name);
        if (resourceUrl == null) {
            LOGGER.error("Resource not found: " + name);
            return new ImageIcon();
        }
        return new ImageIcon(resourceUrl);
    }

    private static BufferedImage getImage(String name) {
        URL resourceUrl = getResourceUrl(name);
        if (resourceUrl == null) {
            LOGGER.error("Resource not found: " + name);
            return new BufferedImage(0, 0, BufferedImage.TYPE_CUSTOM);
        }
        try {
            return ImageIO.read(resourceUrl);
        } catch (IOException e) {
            LOGGER.error("Cannot read the image: " + resourceUrl, e);
            return new BufferedImage(0, 0, BufferedImage.TYPE_CUSTOM);
        }
    }

    private static URL getResourceUrl(String name) {
        String resourceName = "icons/" + name;
        return IconRegistry.class.getClassLoader().getResource(resourceName);
    }
}
