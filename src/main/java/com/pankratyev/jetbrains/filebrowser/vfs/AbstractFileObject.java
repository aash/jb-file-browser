package com.pankratyev.jetbrains.filebrowser.vfs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Base class for implementations of {@link FileObject}.
 */
public abstract class AbstractFileObject implements FileObject {
    private final String absolutePath;
    private final boolean isDirectory;
    private FileObject parent; // not final since it may be lazy-computed

    protected AbstractFileObject(@Nonnull String absolutePath, @Nullable FileObject parent, boolean isDirectory) {
        this.absolutePath = Objects.requireNonNull(absolutePath);
        this.parent = parent;
        this.isDirectory = isDirectory;
    }

    @Nonnull
    @Override
    public String getFullName() {
        return absolutePath;
    }

    @Nullable
    @Override
    public FileObject getParent() {
        return parent;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Nonnull
    @Override
    public String getName() {
        return VfsUtils.getNameFromAbsolutePath(absolutePath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractFileObject object = (AbstractFileObject) o;

        //noinspection SimplifiableIfStatement
        if (isDirectory != object.isDirectory) {
            return false;
        }
        return absolutePath.equals(object.absolutePath);
    }

    @Override
    public int hashCode() {
        int result = absolutePath.hashCode();
        result = 31 * result + (isDirectory ? 1 : 0);
        return result;
    }

    protected void setParent(FileObject parent) {
        this.parent = parent;
    }
}
