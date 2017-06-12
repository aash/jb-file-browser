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

    protected AbstractFileObject(String absolutePath, FileObject parent, boolean isDirectory) {
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
    public boolean isZipArchive() {
        //TODO make it more reliable
        return getName().toLowerCase().endsWith(".zip");
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

    /**
     * {@link FileObject}s can be compared to properly order a list of objects in UI.
     */
    @Override
    public int compareTo(@Nonnull FileObject another) {
        if (isDirectory() && !another.isDirectory()) {
            return -1;
        }
        if (another.isDirectory() && !isDirectory()) {
            return 1;
        }
        return getName().compareToIgnoreCase(another.getName());
    }

    protected void setParent(FileObject parent) {
        this.parent = parent;
    }
}
