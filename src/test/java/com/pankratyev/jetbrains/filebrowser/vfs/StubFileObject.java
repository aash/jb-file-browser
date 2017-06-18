package com.pankratyev.jetbrains.filebrowser.vfs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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

    @Override
    public boolean hasParent() {
        return false;
    }

    @Nullable
    @Override
    public FileObject getParent() {
        return null;
    }

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        return null;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
