package com.pankratyev.jetbrains.filebrowser;

import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class TestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    /**
     * @param files list of files/directories to delete. If all the files are placed in one directory it's enough to
     * pass only this directory.
     */
    public static void deleteFiles(Path... files) {
        if (files == null) {
            return;
        }
        for (Path file : files) {
            if (file == null) {
                continue;
            }
            if (Files.isDirectory(file)) {
                try {
                    Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            delete(dir);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    LOGGER.warn(null, e);
                }
            } else if (Files.exists(file)) {
                delete(file);
            }
        }
    }

    public static void zipDirectory(final Path directoryToZip, Path resultArchivePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(resultArchivePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walkFileTree(directoryToZip, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(directoryToZip.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(
                            directoryToZip.relativize(dir).toString() + ZipUtils.ZIP_PATH_SEPARATOR));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private static void delete(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            LOGGER.warn("Cannot delete temp file/directory: " + file, e);
        }
    }
}
