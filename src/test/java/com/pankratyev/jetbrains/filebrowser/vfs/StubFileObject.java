package com.pankratyev.jetbrains.filebrowser.vfs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class StubFileObject implements FileObject {
    @Nonnull
    @Override
    public String getName() {
        return "";
    }

    @Nonnull
    @Override
    public String getFullName() {
        return "";
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Nullable
    @Override
    public FileObject getParent() {
        return null;
    }

    @Nullable
    @Override
    public Collection<FileObject> getChildren() throws IOException {
        return null;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
