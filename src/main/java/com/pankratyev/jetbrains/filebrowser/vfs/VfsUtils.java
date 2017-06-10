package com.pankratyev.jetbrains.filebrowser.vfs;

import javax.annotation.Nonnull;

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
}
