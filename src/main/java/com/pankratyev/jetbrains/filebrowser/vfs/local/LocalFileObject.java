package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public final class LocalFileObject extends AbstractFileObject {
    protected LocalFileObject(String absolutePath, FileObject parent, boolean isDirectory) {
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
}
