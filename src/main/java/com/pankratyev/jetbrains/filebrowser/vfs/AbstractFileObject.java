package com.pankratyev.jetbrains.filebrowser.vfs;

import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Base class for implementations of {@link FileObject}.
 */
public abstract class AbstractFileObject implements FileObject {
    private final String absolutePath;
    private final FileObject parent;
    private final boolean isDirectory;

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
        return FilenameUtils.getName(absolutePath);
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
