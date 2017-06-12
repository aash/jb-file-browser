package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * Represents a file (or directory) located on FTP server.
 * Absolute path of such file will contain <code>ftp://_host_:_port_</code> prefix.
 */
public final class FtpFileObject extends AbstractFileObject {
    //TODO probably should support reconnecting so client should be wrapped with some facade
    private final FTPClient client;

    /**
     * @param client initialized FTP client with established connection.
     * @param absolutePath absolute path to this file/directory on FTP server.
     * @param parent parent directory in FTP server.
     * @param isDirectory whether this {@link FileObject} is a directory.
     */
    public FtpFileObject(@Nonnull FTPClient client, String absolutePath, FileObject parent, boolean isDirectory) {
        super(absolutePath, parent, isDirectory);
        this.client = client;
    }

    @Nullable
    @Override
    public Collection<FileObject> getChildren() throws IOException {
        FTPFile[] files = client.listFiles();
        List<FileObject> children = new ArrayList<>();
        for (FTPFile file : files) {
            String fileAbsolutePath = getFullName() + File.separator + file.getName();
            children.add(new FtpFileObject(client, fileAbsolutePath, this, file.isDirectory()));
        }
        return children;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        return client.retrieveFileStream(getFullName());
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        throw new UnsupportedOperationException(); //TODO implement
    }
}
