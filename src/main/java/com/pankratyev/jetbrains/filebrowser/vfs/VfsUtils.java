package com.pankratyev.jetbrains.filebrowser.vfs;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class VfsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(VfsUtils.class);

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

    public static void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            LOGGER.warn("Cannot delete a file {}", file);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        try {
                            Files.delete(dir);
                        } catch (IOException e) {
                            LOGGER.warn("Cannot delete a directory {}", dir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                LOGGER.warn(null, e);
            }
        } else if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                LOGGER.warn("Cannot delete a file {}", path);
            }
        }
    }
}
