package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.zip.ZipFile;

/**
 * Represents a file (or directory) located on FTP server.
 */
public final class FtpFileObject extends AbstractFileObject {
    public FtpFileObject(String absolutePath, FileObject parent, boolean isDirectory) {
        super(absolutePath, parent, isDirectory);
    }

    @Nullable
    @Override
    public Collection<FileObject> getChildren() throws IOException {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException(); //TODO implement
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException(); //TODO implement
    }
}
