package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZipUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.zip.ZippedFileObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

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
    LocalFileObject(@Nullable LocalFileObject parent, @Nonnull Path path) {
        super(path.toAbsolutePath().toString(), parent, Files.isDirectory(path));
        this.path = path; // TODO actually parent can be obtained from path; remove it?
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
        if (isZipArchive()) {
            return getZipArchiveChildren();
        }
        return null;
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            return null;
        }
        //TODO size limit
        return new BufferedInputStream(Files.newInputStream(path));
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        if (!isZipArchive()) {
            throw new IllegalStateException("Not a zip archive: " + this);
        }
        return new ZipFile(path.toFile());
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

    private List<FileObject> getZipArchiveChildren() throws IOException {
        try (ZipFile asZipFile = toZipFile()) {
            List<FileObject> archiveContents = ZipUtils.getAllZipChildren(this, asZipFile);
            for (Iterator<FileObject> iter = archiveContents.iterator(); iter.hasNext(); ) {
                ZippedFileObject zippedFileObject = (ZippedFileObject) iter.next();
                if (ZipUtils.getNestingLevel(zippedFileObject.getPathInArchive()) > 0) {
                    // leave only top-level files in archive
                    iter.remove();
                }
            }
            return archiveContents;
        }
    }

    @Override
    public String toString() {
        return "LocalFileObject{" +
                "path=" + path +
                '}';
    }
}
