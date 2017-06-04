package com.pankratyev.jetbrains.filebrowser.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import java.net.URL;

public final class IconRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(IconRegistry.class);

    private IconRegistry() {
    }


    public static final ImageIcon FOLDER = getIcon("folder.png");
    public static final ImageIcon IMAGE = getIcon("image.png");
    public static final ImageIcon TEXT_FILE = getIcon("text-file.png");
    public static final ImageIcon PLAIN_FILE = getIcon("plain-file.png");


    private static ImageIcon getIcon(String iconName) {
        String resourceName = "icons/" + iconName;
        URL resourceUrl = IconRegistry.class.getClassLoader().getResource(resourceName);
        if (resourceUrl == null) {
            LOGGER.error("Resource not found: " + resourceName);
            return new ImageIcon();
        }
        return new ImageIcon(resourceUrl);
    }
}
