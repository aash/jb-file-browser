package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.AbstractFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Represents a file (or directory) inside zip archive. Zip archive itself is NOT a {@link ZippedFileObject}.
 */
public final class ZippedFileObject extends AbstractFileObject {
    /**
     * If this file size in archive is greater than this limit {@link #getInputStream()} will return null.
     */
    private static final int ZIP_ENTRY_SIZE_LIMIT = 30 * 1024 * 1024; // bytes

    private final FileObject parentZipArchive;
    private final ZipFileProvider parentArchiveZipFileProvider;
    private final String pathInArchive;

    /**
     * @param parentZipArchive archive where this file is placed.
     * @param pathInArchive path in archive. It MUST be exactly the same value that {@link ZipEntry#getName()} returned.
     * @param isDirectory whether this {@link FileObject} is a directory.
     * @param parent parent of this {@link FileObject}; it may be a directory in the archive or archive itself (in last
*               case this is the same {@link FileObject} that {@link #parentZipArchive}.
     * @param parentArchiveZipFileProvider {@link ZipFile} provider for parent archive.
     */
    ZippedFileObject(FileObject parentZipArchive, String pathInArchive, ZipFileProvider parentArchiveZipFileProvider,
            boolean isDirectory, FileObject parent) {
        super(getAbsolutePath(Objects.requireNonNull(parentZipArchive), Objects.requireNonNull(pathInArchive)),
                Objects.requireNonNull(parent), isDirectory);

        if (!ZipUtils.isZipArchive(parentZipArchive)) {
            throw new IllegalArgumentException(parentZipArchive.toString());
        }

        this.parentZipArchive = parentZipArchive;
        this.parentArchiveZipFileProvider = Objects.requireNonNull(parentArchiveZipFileProvider);
        this.pathInArchive = pathInArchive;
    }

    @Override
    public boolean hasParent() {
        return true; // zipped file always has a parent
    }

    @Nonnull
    @Override
    protected List<FileObject> getDirectoryChildren() throws IOException {
        try (ZipFile parentAsZipFile = getParentArchiveZipFile()) {
            List<FileObject> archiveContents = ZipUtils.getAllZipChildren(
                    parentZipArchive, parentAsZipFile, parentArchiveZipFileProvider);
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

    @Nonnull
    @Override
    protected List<FileObject> getZipChildren() throws IOException {
        return ZipUtils.getZipArchiveTopLevelChildren(this);
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        if (isDirectory()) {
            return null;
        }

        try (ZipFile zip = getParentArchiveZipFile()) {
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

    ZipFile getParentArchiveZipFile() throws IOException {
        return parentArchiveZipFileProvider.getZipFile();
    }

    private static String getAbsolutePath(FileObject parentZipArchive, String pathInArchive) {
        pathInArchive = pathInArchive.replace(ZipUtils.ZIP_PATH_SEPARATOR, File.separator);
        if (!pathInArchive.startsWith(File.separator)) {
            pathInArchive = File.separator + pathInArchive;
        }
        return parentZipArchive.getFullName() + pathInArchive;
    }

    @Nonnull
    String getPathInArchive() {
        return pathInArchive;
    }

    @Override
    public String toString() {
        return "ZippedFileObject{" +
                "parentZipArchive=" + parentZipArchive +
                ", pathInArchive='" + pathInArchive + '\'' +
                '}';
    }
}
