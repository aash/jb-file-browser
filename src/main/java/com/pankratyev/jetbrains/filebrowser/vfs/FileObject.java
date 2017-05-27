package com.pankratyev.jetbrains.filebrowser.vfs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Represents an abstract file (or directory).
 * Some methods ({@link #getChildren()}, {@link #getInputStream()}) work only for files or directories, not for both.
 * This makes UI implementation easier; contracts of such methods designed the way it shouldn't bring any problems.
 */
public interface FileObject {
    /**
     * @return name of this file or directory, i.e. the last segment in the full path.
     */
    @Nonnull
    String getName();

    /**
     * @return full name of this file or directory.
     */
    @Nonnull
    String getFullName();

    /**
     * @return true if this file is a directory; false otherwise.
     */
    boolean isDirectory();

    /**
     * @return parent of this file/directory or null if none present (e.g. if this is a root directory).
     */
    @Nullable
    FileObject getParent();

    /**
     * @return child files/directories if this {@link FileObject} is a directory; null otherwise.
     * @throws IOException on any I/O errors.
     */
    @Nullable
    Collection<FileObject> getChildren() throws IOException;

    /**
     * @return input stream for this {@link FileObject} if it is a file; null if it is a directory.
     * @throws IOException on any I/O errors.
     */
    @Nullable
    InputStream getInputStream() throws IOException;
}
