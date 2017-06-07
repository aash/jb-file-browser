package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Represents a file (or directory) inside zip archive. Zip archive itself is NOT a {@link ZippedFileObject}.
 */
public final class ZippedFileObject extends AbstractFileObject {
    private final Collection<FileObject> children;

    public ZippedFileObject(String absolutePath, FileObject parent, boolean isDirectory, Collection<FileObject> children) {
        super(absolutePath, parent, isDirectory);
        this.children = children;
    }

    @Nullable
    @Override
    public Collection<FileObject> getChildren() throws IOException {
        return children;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        //TODO implement; probably must save top-level parent to do so
        throw new UnsupportedOperationException();
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
