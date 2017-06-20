package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a file (or directory) located on local file system.
 * Use {@link LocalFileObjectFactory} to obtain an instance of this class.
 */
public final class LocalFileObject extends AbstractFileObject {
    private final Path path;

    /**
     * @param parent parent of this {@link FileObject}; may be null if this {@link FileObject} doesn't have a parent.
     * @param path local file path.
     */
    LocalFileObject(@Nullable FileObject parent, @Nonnull Path path) {
        super(path.toAbsolutePath().toString(), parent, Files.isDirectory(path));
        this.path = path;
    }

    @Override
    public boolean hasParent() {
        return getParent() != null;
    }

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        if (isDirectory()) {
            return getDirectoryChildren();
        }
        if (ZipUtils.isZipArchive(this)) {
            return ZipUtils.getZipArchiveTopLevelChildren(this);
        }
        return null;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            return null;
        }
        return new BufferedInputStream(Files.newInputStream(path));
    }

    private List<FileObject> getDirectoryChildren() throws IOException {
        List<FileObject> children = new ArrayList<>();
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
            for (Path child : dirStream) {
                if (!isJunction(child)) {
                    children.add(new LocalFileObject(this, child));
                }
            }
        }
        return children;
    }

    private static boolean isJunction(Path path) {
        // a dirty way to define whether the path is a Junction
        try {
            return !Files.isReadable(path) && path.compareTo(path.toRealPath()) != 0;
        } catch (IOException ignore) {
            return false;
        }
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "LocalFileObject{" +
                "path=" + path +
                '}';
    }
}
