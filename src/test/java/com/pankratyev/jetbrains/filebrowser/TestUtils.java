package com.pankratyev.jetbrains.filebrowser;

import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;

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
            VfsUtils.deleteQuietly(file);
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
}
