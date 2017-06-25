package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.ftp.FtpFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObject;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.ArchiveFileType;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.provider.FileTypeProvider;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.provider.ExtensionBasedFileTypeProvider;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipUtils {
    /**
     * ZIP spec (4.4.17 file name): All slashes MUST be forward slashes '/' as opposed to
     * backwards slashes '\' for compatibility with Amiga and UNIX file systems etc.
     */
    public static final String ZIP_PATH_SEPARATOR = "/";

    private static final FileTypeProvider FILE_TYPE_PROVIDER = new ExtensionBasedFileTypeProvider();

    private ZipUtils() {
    }


    /**
     * @return true if this {@link FileObject} is a zip archive; false otherwise.
     */
    public static boolean isZipArchive(@Nonnull FileObject fileObject) {
        return FILE_TYPE_PROVIDER.getType(fileObject) instanceof ArchiveFileType;
    }


    /**
     * @param archive local zip archive.
     * @return zip archive top-level files/directories.
     */
    @Nonnull
    public static List<FileObject> getZipArchiveTopLevelChildren(@Nonnull LocalFileObject archive) throws IOException {
        return doGetZipArchiveTopLevelChildren(archive, new LocalArchiveZipFileProvider(archive));
    }

    /**
     * @param archive zip archive placed on FTP server.
     * @return zip archive top-level files/directories.
     */
    @Nonnull
    public static List<FileObject> getZipArchiveTopLevelChildren(@Nonnull FtpFileObject archive) throws IOException {
        return doGetZipArchiveTopLevelChildren(archive, new FtpArchiveZipFileProvider(archive));
    }

    /**
     * @param archive zip archive placed in parent zip archive.
     * @return zip archive top-level files/directories.
     */
    @Nonnull
    static List<FileObject> getZipArchiveTopLevelChildren(@Nonnull ZippedFileObject archive) throws IOException {
        return doGetZipArchiveTopLevelChildren(archive, new ZippedArchiveZipFileProvider(archive));
    }


    private static List<FileObject> doGetZipArchiveTopLevelChildren(FileObject archive,
            ZipFileProvider archiveZipFileProvider) throws IOException {
        try (ZipFile zipFile = archiveZipFileProvider.getZipFile()) {
            List<FileObject> archiveContents = ZipUtils.getAllZipChildren(archive, zipFile, archiveZipFileProvider);

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


    /**
     * @param asFileObject zip archive represented as {@link FileObject}.
     * @param asZipFile zip archive represented as {@link ZipFile}; must not be closed.
     * @return zip archive contents (not only top level items but all of them).
     */
    @Nonnull
    static List<FileObject> getAllZipChildren(@Nonnull FileObject asFileObject, @Nonnull ZipFile asZipFile,
            ZipFileProvider archiveZipFileProvider) {
        Enumeration<? extends ZipEntry> entriesEnumeration = asZipFile.entries();

        Map<String, ZipEntry> entriesByPaths = new HashMap<>();
        while (entriesEnumeration.hasMoreElements()) {
            ZipEntry entry = entriesEnumeration.nextElement();
            String name = entry.getName();
            if (!name.equals(ZIP_PATH_SEPARATOR)) {
                entriesByPaths.put(name, entry);
            }
        }

        return processEntries(entriesByPaths, asFileObject, archiveZipFileProvider);
    }

    private static List<FileObject> processEntries(Map<String, ZipEntry> entriesByPaths, FileObject parentArchive,
            ZipFileProvider archiveZipFileProvider) {
        int currentNestingLevel = 0;
        Map<String, FileObject> fileObjectsByPaths = new HashMap<>();

        // the idea is to iteratively process entries starting from top-level ones
        while (!entriesByPaths.isEmpty()) {
            for (Iterator<Map.Entry<String, ZipEntry>> iter = entriesByPaths.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry<String, ZipEntry> entry = iter.next();

                String path = entry.getKey();
                if (path.equals(ZIP_PATH_SEPARATOR)) {
                    // don't process '/' entry
                    iter.remove();
                    continue;
                }

                int pathNestingLevel = getNestingLevel(path);

                if (pathNestingLevel == currentNestingLevel) {
                    iter.remove(); // element is processed

                    String normalizedPath = VfsUtils.normalizePath(path, ZIP_PATH_SEPARATOR);
                    String parentDirPath = StringUtils.substringBeforeLast(normalizedPath, ZIP_PATH_SEPARATOR);
                    FileObject parent = fileObjectsByPaths.get(parentDirPath);
                    if (parent == null) {
                        parent = parentArchive;
                    }

                    ZippedFileObject fileObject = new ZippedFileObject(
                            parentArchive, path, archiveZipFileProvider, entry.getValue().isDirectory(), parent);
                    fileObjectsByPaths.put(normalizedPath, fileObject);
                }
            }

            currentNestingLevel++;
        }

        return new ArrayList<>(fileObjectsByPaths.values());
    }


    /**
     * @param path path in archive (with {@link #ZIP_PATH_SEPARATOR} separators).
     * @return nesting level of passed path (0 for top-level paths).
     */
    static int getNestingLevel(@Nonnull String path) {
        path = VfsUtils.normalizePath(path, ZIP_PATH_SEPARATOR);
        return StringUtils.countMatches(path, ZIP_PATH_SEPARATOR);
    }


    public static final class LocalArchiveZipFileProvider implements ZipFileProvider {
        private final LocalFileObject fileObject;

        public LocalArchiveZipFileProvider(@Nonnull LocalFileObject fileObject) {
            this.fileObject = Objects.requireNonNull(fileObject);
        }

        @Nonnull
        @Override
        public ZipFile getZipFile() throws IOException {
            if (!isZipArchive(fileObject)) {
                throw new IllegalStateException("Not a zip archive: " + this);
            }
            return new ZipFile(fileObject.getPath().toFile());
        }
    }

    public static final class FtpArchiveZipFileProvider implements ZipFileProvider {
        private final FtpFileObject fileObject;

        FtpArchiveZipFileProvider(@Nonnull FtpFileObject fileObject) {
            this.fileObject = Objects.requireNonNull(fileObject);
        }

        @Nonnull
        @Override
        public ZipFile getZipFile() throws IOException {
            if (!isZipArchive(fileObject)) {
                throw new IllegalStateException("Not a zip archive: " + this);
            }

            Path localCopy = fileObject.getLocalCopy();
            if (localCopy == null) {
                throw new IOException("No local copy available for archive");
            }
            return new ZipFile(localCopy.toFile());
        }
    }

    public static final class ZippedArchiveZipFileProvider implements ZipFileProvider {
        private final ZippedFileObject fileObject;

        ZippedArchiveZipFileProvider(@Nonnull ZippedFileObject fileObject) {
            this.fileObject = Objects.requireNonNull(fileObject);
        }

        @Nonnull
        @Override
        public ZipFile getZipFile() throws IOException {
            if (!isZipArchive(fileObject)) {
                throw new IllegalStateException("Not a zip archive: " + this);
            }

            File extractedSubArchive = fileObject.extractSubArchive().toFile();
            extractedSubArchive.deleteOnExit();
            return new ZipFile(extractedSubArchive);
        }
    }
}
