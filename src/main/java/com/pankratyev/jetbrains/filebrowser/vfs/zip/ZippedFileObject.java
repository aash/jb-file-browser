package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a file (or directory) inside zip archive. Zip archive itself is NOT a {@link ZippedFileObject}.
 */
public final class ZippedFileObject extends AbstractFileObject {
    /**
     * If this file size in archive is greater than this limit in archive {@link #getInputStream()} will return null.
     */
    private static final int ZIP_ENTRY_SIZE_LIMIT = 30 * 1024 * 1024; // bytes

    private final FileObject parentZipArchive;
    private final String pathInArchive;

    /**
     * @param parentZipArchive archive where this file is placed.
     * @param pathInArchive path in archive. It MUST be exactly the same value that {@link ZipEntry#getName()} returned.
     * @param isDirectory whether this {@link FileObject} is a directory.
     * @param parent parent of this {@link FileObject}; it may be a directory in the archive or archive itself (in last
     *               case this is the same {@link FileObject} that {@link #parentZipArchive}.
     */
    public ZippedFileObject(FileObject parentZipArchive, String pathInArchive, boolean isDirectory, FileObject parent) {
        super(getAbsolutePath(Objects.requireNonNull(parentZipArchive), Objects.requireNonNull(pathInArchive)),
                Objects.requireNonNull(parent), isDirectory);
        if (!parentZipArchive.isZipArchive()) {
            throw new IllegalArgumentException(parentZipArchive.toString());
        }
        this.parentZipArchive = parentZipArchive;
        this.pathInArchive = pathInArchive;
    }

    @Nullable
    @Override
    public Collection<FileObject> getChildren() throws IOException {
        //TODO it would be good to support zipped zip archives
        if (!isDirectory()) {
            return null;
        }

        try (ZipFile parentAsZipFile = parentZipArchive.toZipFile()) {
            List<FileObject> archiveContents = ZipUtils.getAllZipChildren(parentZipArchive, parentAsZipFile);
            List<FileObject> resultChildren = new ArrayList<>();
            for (FileObject zippedFileObject : archiveContents) {
                String pathInArchive = ((ZippedFileObject) zippedFileObject).getPathInArchive();
                // leave only direct children of current zipped directory
                if (pathInArchive.startsWith(getPathInArchive()) &&
                        ZipUtils.getNestingLevel(pathInArchive) == ZipUtils.getNestingLevel(getPathInArchive()) + 1) {
                    resultChildren.add(zippedFileObject);
                }
            }

            return resultChildren;
        }
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            return null;
        }

        try (ZipFile zip = parentZipArchive.toZipFile()) {
            ZipEntry entry = zip.getEntry(pathInArchive);
            if (entry == null) {
                return null;
            }

            if (entry.getSize() > ZIP_ENTRY_SIZE_LIMIT) {
                return null;
            }

            // read file from zip archive in memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream is = new BufferedInputStream(zip.getInputStream(entry))) {
                int copiedBytesCount = IOUtils.copy(is, baos);
                if (copiedBytesCount < 0) {
                    return null;
                }
            }
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        throw new UnsupportedOperationException("Cannot represent zipped file as ZipFile");
    }

    private static String getAbsolutePath(FileObject parentZipArchive, String pathInArchive) {
        //TODO replace '/' in pathInArchive with PATH_SEPARATOR
        if (!pathInArchive.startsWith(PATH_SEPARATOR)) {
            pathInArchive = PATH_SEPARATOR + pathInArchive;
        }
        return parentZipArchive.getFullName() + pathInArchive;
    }

    @Nonnull
    public String getPathInArchive() {
        return pathInArchive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ZippedFileObject object = (ZippedFileObject) o;

        //noinspection SimplifiableIfStatement
        if (!parentZipArchive.equals(object.parentZipArchive)) {
            return false;
        }
        return pathInArchive.equals(object.pathInArchive);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + parentZipArchive.hashCode();
        result = 31 * result + pathInArchive.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ZippedFileObject{" +
                "parentZipArchive=" + parentZipArchive +
                ", pathInArchive='" + pathInArchive + '\'' +
                '}';
    }
}
