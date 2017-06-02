package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Represents a file (or directory) located on local file system.
 */
public final class LocalFileObject extends AbstractFileObject {
    private final Path path;

    public LocalFileObject(@Nullable LocalFileObject parent, @Nonnull Path path) {
        super(path.toAbsolutePath().toString(), parent, Files.isDirectory(path));
        this.path = path;
    }

    @Nullable
    @Override
    public Collection<FileObject> getChildren() throws IOException {
        if (isDirectory()) {
            return getDirectoryChildren();
        }
        if (isZipFile()) {
            return getZipChildren();
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
                children.add(new LocalFileObject(this, child));
            }
        }
        return children;
    }

    // TODO consider moving isZipFile() and getZipChildren() to AbstractFileObject

    private boolean isZipFile() {
        //TODO make it more reliable
        return getName().toLowerCase().endsWith(".zip");
    }

    private Collection<FileObject> getZipChildren() throws IOException {
        //noinspection ConstantConditions - getInputStream() shouldn't return null if this method is invoked
        try (ZipInputStream zis = new ZipInputStream(getInputStream())) {
            List<ZipEntry> entries = new ArrayList<>();
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                entries.add(entry);
            }

            Map<String, FileObject> fileObjectsByPaths = new HashMap<>();
            for (ZipEntry e : entries) {
                //TODO implement
            }

            return fileObjectsByPaths.values();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocalFileObject object = (LocalFileObject) o;

        return path.equals(object.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
