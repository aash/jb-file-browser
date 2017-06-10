package com.pankratyev.jetbrains.filebrowser.vfs;

import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.io.File;

public final class VfsUtils {
    private VfsUtils() {
    }

    /**
     * Removes trailing and leading slashes if present.
     */
    public static String normalizePath(@Nonnull String path, @Nonnull String separator) {
        if (path.startsWith(separator)) {
            path = path.substring(1);
        }
        if (path.endsWith(separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String getNameFromAbsolutePath(@Nonnull String absolutePath) {
        return FilenameUtils.getName(absolutePath.endsWith(File.separator)
                ? absolutePath.substring(0, absolutePath.length() - 1) : absolutePath);
    }
}
