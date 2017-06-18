package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility class to manage files cached to local disk from FTP server.
 */
public final class LocalCopyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalCopyManager.class);

    static final String TEMP_DIRECTORY_BASE_NAME = "jetbrains_filebrowser_pankratyev";

    /**
     * If a local copy was created more than this time interval ago it will be considered outdated.
     */
    private static final int LOCAL_COPY_EXPIRE_TIME_INTERVAL = 3 * 60 * 1000; // millis

    private final Path basePath;

    LocalCopyManager(@Nonnull String host) {
        this.basePath = getBasePath(host);
    }

    /**
     * Checks if for passed {@link FtpFileObject} a local file copy is available and returns an {@link InputStream}
     * for it.
     * @param fileObject file located on FTP server to get local copy for.
     * @return locally stored copy of file; or null if no local copy found or some I/O errors occurred.
     */
    @Nullable
    Path getLocalCopy(@Nonnull FtpFileObject fileObject) {
        if (fileObject.isDirectory()) {
            throw new IllegalArgumentException("Local copy requested for a directory: " + fileObject);
        }

        try {
            ensureBasePathExists();

            Path localCopy = getLocalCopyPath(fileObject);
            if (Files.exists(localCopy)) {
                if (!checkLocalCopyIsValid(localCopy)) {
                    Files.delete(localCopy);
                } else {
                    LOGGER.debug("Local copy found: " + localCopy);
                    return localCopy;
                }
            }
        } catch (IOException e) {
            LOGGER.warn("An error occurred while trying to obtain a local file copy", e);
        }

        return null;
    }

    /**
     * Provides an {@link OutputStream} to store a local copy for passed {@link FileObject}.
     * @param fileObject file object to store local copy of.
     * @return output stream which should be used to write file content to; or null on any I/O errors.
     * @see FtpFileObject#getInputStream()
     */
    @Nullable
    OutputStream getLocalCopyOutputStream(@Nonnull FtpFileObject fileObject) {
        try {
            ensureBasePathExists();
            Path localCopyPath = getLocalCopyPath(fileObject);
            Files.createDirectories(localCopyPath.getParent());
            LOGGER.debug("Returning local copy OS: " + localCopyPath);
            return new BufferedOutputStream(Files.newOutputStream(localCopyPath));
        } catch (IOException e) {
            LOGGER.warn("Unable to open a local copy to write", e);
        }
        return null;
    }

    private boolean checkLocalCopyIsValid(Path localCopy) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(localCopy, BasicFileAttributes.class);
        long currentTime = System.currentTimeMillis();

        //noinspection RedundantIfStatement
        if (currentTime - attrs.creationTime().toMillis() > LOCAL_COPY_EXPIRE_TIME_INTERVAL) {
            return false;
        }

        return true;
    }

    private Path getLocalCopyPath(FtpFileObject fileObject) {
        String fullName = fileObject.getFullName().substring(File.separator.length());
        return basePath.resolve(fullName);
    }

    private void ensureBasePathExists() throws IOException {
        if (!Files.exists(basePath)) {
            try {
                Files.createDirectories(basePath);
            } catch (FileAlreadyExistsException ignore) { // created concurrently
            }
        }
    }

    private static Path getBasePath(String host) {
        String dirPath = FileUtils.getTempDirectoryPath();
        if (!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }
        dirPath += TEMP_DIRECTORY_BASE_NAME + File.separator + host;
        return Paths.get(dirPath);
    }

    /**
     * Removes file local copies for passed path and current host.
     * @param fullPath full path on FTP server under which all the file local copies should be removed.
     */
    public void invalidate(String fullPath) {
        if (!Files.exists(basePath)) {
            return;
        }
        Path pathToDelete = basePath.resolve(fullPath.substring(File.separator.length()));
        LOGGER.debug("Removing {}", pathToDelete);
        VfsUtils.deleteQuietly(pathToDelete);
    }
}
