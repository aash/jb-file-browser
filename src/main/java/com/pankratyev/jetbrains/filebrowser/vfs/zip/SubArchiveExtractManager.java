package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class SubArchiveExtractManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubArchiveExtractManager.class);

    private static final Path BASE_DIRECTORY = Paths.get(VfsUtils.getBaseTempDir());
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                VfsUtils.deleteQuietly(BASE_DIRECTORY);
            }
        }));
    }

    private Path getTempFileFor(ZippedFileObject fileObject) {
        return BASE_DIRECTORY.resolve(fileObject.getFullName().substring(File.separator.length()));
    }

    Path getExtractedSubArchive(ZippedFileObject archive) throws IOException {
        if (!ZipUtils.isZipArchive(archive)) {
            throw new IllegalArgumentException("Not a zip archive: " + archive);
        }

        Path tempFile = getTempFileFor(archive);
        if (Files.exists(tempFile)) {
            LOGGER.debug("Extracted sub-archive found: " + tempFile);
            return tempFile;
        }

        try (ZipFile zip = archive.getParentArchiveZipFile()) {
            String pathInArchive = archive.getPathInArchive();
            ZipEntry entry = zip.getEntry(pathInArchive);
            if (entry == null) {
                throw new IOException(pathInArchive + " not found in " + zip.getName());
            }

            Files.createDirectories(tempFile.getParent());
            LOGGER.debug("Extracting sub-archive: " + tempFile);
            try (OutputStream nestedArchiveOs = new BufferedOutputStream(Files.newOutputStream(tempFile))) {
                IOUtils.copy(new BufferedInputStream(zip.getInputStream(entry)), nestedArchiveOs);
            } catch (IOException e) {
                VfsUtils.deleteQuietly(tempFile);
                throw e;
            }

            return tempFile;
        }
    }
}
