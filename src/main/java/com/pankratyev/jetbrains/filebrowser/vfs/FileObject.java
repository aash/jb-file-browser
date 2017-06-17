package com.pankratyev.jetbrains.filebrowser.vfs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Represents an abstract file (or directory).
 * Some methods ({@link #getChildren()}, {@link #getInputStream()}) work only for specific files (zip archives) or
 * directories. This makes UI implementation easier; these methods designed the way it shouldn't bring any problems.
 */
public interface FileObject {
    /**
     * @return name of this file or directory, i.e. the last segment in the full path (including extension).
     */
    @Nonnull
    String getName();

    /**
     * @return full name (absolute path) of this file or directory.
     */
    @Nonnull
    String getFullName();

    /**
     * @return true if this {@link FileObject} is a directory; false otherwise.
     */
    boolean isDirectory();

    /**
     * @return true if this {@link FileObject} has a parent; false otherwise.
     */
    boolean hasParent();

    /**
     * @return parent of this file/directory or null if none present (e.g. if this is a root directory).
     * @see #hasParent()
     */
    @Nullable
    FileObject getParent();

    /**
     * @return child files/directories if this {@link FileObject} is a directory or a zip archive; null otherwise.
     * @throws IOException on any I/O errors.
     */
    @Nullable
    List<FileObject> getChildren() throws IOException;

    /**
     * @return input stream for this {@link FileObject} if it is a file; null if it is a directory.
     * @throws IOException on any I/O errors.
     */
    @Nullable
    InputStream getInputStream() throws IOException;
}
