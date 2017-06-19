package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Represents a file (or directory) located on FTP server.
 * Absolute path of such file will contain <code>ftp://_host_:_port_</code> prefix.
 */
public final class FtpFileObject extends AbstractFileObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpFileObject.class);

    private final FtpClient client;
    private final LocalCopyManager localCopyManager;

    /**
     * @param client initialized FTP client with established connection.
     * @param absolutePath absolute path to this file/directory on FTP server.
     * @param parent parent directory in FTP server. May be null in which case it will be lazy-computed.
     * @param isDirectory whether this {@link FileObject} is a directory.
     * @param localCopyManager local copy manager for target host.
     */
    FtpFileObject(@Nonnull FtpClient client, String absolutePath, FileObject parent, boolean isDirectory,
            @Nonnull LocalCopyManager localCopyManager) {
        super(absolutePath, parent, isDirectory);
        this.client = Objects.requireNonNull(client);
        this.localCopyManager = Objects.requireNonNull(localCopyManager);
    }


    @Override
    public boolean hasParent() {
        return true;
    }

    @Nullable
    @Override
    public FileObject getParent() {
        if (super.getParent() == null) {
            try {
                setParent(client.getParentDirectory(this));
            } catch (IOException e) {
                LOGGER.warn("Cannot get the parent of FTP file object", e);
            }
        }
        return super.getParent();
    }

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        if (isDirectory()) {
            return client.list(this);
        }

        if (ZipUtils.isZipArchive(this)) {
            Path localCopy = localCopyManager.getLocalCopy(this);
            if (localCopy == null) {
                LOGGER.debug("Storing local copy to get children of {}", this);

                try (OutputStream localCopyOs = localCopyManager.getLocalCopyOutputStream(this)) {
                    if (localCopyOs == null) {
                        throw new IOException("Unable to store a local copy of " + this);
                    }

                    LOGGER.debug("Local copy will be stored for {}", this);

                    client.retrieveFile(this, localCopyOs);
                    localCopy = localCopyManager.getLocalCopy(this);
                } catch (IOException e) {
                    localCopyManager.deleteLocalCopy(this);
                    throw e;
                }
            }

            if (localCopy != null) {
                return ZipUtils.getZipArchiveTopLevelChildren(this);
            }
            throw new IOException("Local copy not found for " + this);
        }

        return null;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            return null;
        }

        Path localCopy = localCopyManager.getLocalCopy(this);
        if (localCopy == null) {
            try (OutputStream localCopyOs = localCopyManager.getLocalCopyOutputStream(this)) {
                if (localCopyOs == null) {
                    // TODO in such case it's possible to use direct FTP stream
                    throw new IOException("Unable to store a local copy of " + this);
                }

                LOGGER.debug("Local copy will be stored for {}", this);
                client.retrieveFile(this, localCopyOs);
                localCopy = localCopyManager.getLocalCopy(this);
            } catch (IOException e) {
                localCopyManager.deleteLocalCopy(this);
                throw e;
            }
        }

        if (localCopy != null) {
            return new BufferedInputStream(Files.newInputStream(localCopy));
        }
        throw new IOException("Local copy not found for " + this);
    }


    public Path getLocalCopy() {
        return localCopyManager.getLocalCopy(this);
    }

    @Override
    public String toString() {
        return "FtpFileObject{" +
                "fullName=" + getFullName() +
                '}';
    }
}
