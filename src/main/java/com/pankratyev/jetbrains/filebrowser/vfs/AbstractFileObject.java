package com.pankratyev.jetbrains.filebrowser.vfs;

import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
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

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        if (isDirectory()) {
            return getDirectoryChildren();
        }
        if (ZipUtils.isZipArchive(this)) {
            return getZipChildren();
        }
        return null;
    }

    @Nonnull
    protected abstract List<FileObject> getDirectoryChildren() throws IOException;

    @Nonnull
    protected abstract List<FileObject> getZipChildren() throws IOException;


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
