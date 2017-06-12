package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * Represents a file (or directory) located on FTP server.
 * Absolute path of such file will contain <code>ftp://_host_:_port_</code> prefix.
 */
public final class FtpFileObject extends AbstractFileObject {
    private final FtpClient client;

    /**
     * @param client initialized FTP client with established connection.
     * @param absolutePath absolute path to this file/directory on FTP server.
     * @param parent parent directory in FTP server. May be null in which case it will be lazy-computed.
     * @param isDirectory whether this {@link FileObject} is a directory.
     */
    FtpFileObject(@Nonnull FtpClient client, String absolutePath, FileObject parent, boolean isDirectory) {
        super(absolutePath, parent, isDirectory);
        this.client = client;
    }

    @Override
    public boolean hasParent() {
        //TODO implement properly
        return true;
    }

    @Nullable
    @Override
    public FileObject getParent() {
        if (super.getParent() == null) {
            try {
                setParent(client.getParentDirectory(this));
            } catch (IOException e) {
                //TODO handle
            }
        }
        return super.getParent();
    }

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        return client.list(this);
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            return null;
        }
        return client.getFileStream(this);
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Override
    public String toString() {
        return "FtpFileObject{" +
                "fullName=" + getFullName() +
                '}';
    }
}
