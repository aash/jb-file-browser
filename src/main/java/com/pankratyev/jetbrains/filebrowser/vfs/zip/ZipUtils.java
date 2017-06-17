package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ZipUtils {
    public static final String ZIP_PATH_SEPARATOR = "/";

    private ZipUtils() {
    }

    /**
     * @return true if this {@link FileObject} is a zip archive; false otherwise.
     */
    public static boolean isZipArchive(@Nonnull FileObject fileObject) {
        //TODO make it more reliable
        return fileObject.getName().toLowerCase().endsWith(".zip");
    }

    /**
     * @param asFileObject zip archive represented as {@link FileObject}.
     * @param asZipFile zip archive represented as {@link ZipFile}; must not be closed.
     * @return zip archive top-level files/directories.
     */
    public static List<FileObject> getZipArchiveTopLevelChildren(
            @Nonnull FileObject asFileObject, @Nonnull ZipFile asZipFile) throws IOException {
        List<FileObject> archiveContents = ZipUtils.getAllZipChildren(asFileObject, asZipFile);
        for (Iterator<FileObject> iter = archiveContents.iterator(); iter.hasNext(); ) {
            ZippedFileObject zippedFileObject = (ZippedFileObject) iter.next();
            if (ZipUtils.getNestingLevel(zippedFileObject.getPathInArchive()) > 0) {
                // leave only top-level files in archive
                iter.remove();
            }
        }
        return archiveContents;
    }

    /**
     * @param asFileObject zip archive represented as {@link FileObject}.
     * @param asZipFile zip archive represented as {@link ZipFile}; must not be closed.
     * @return zip archive contents (not only top level items but all of them).
     */
    @Nonnull
    static List<FileObject> getAllZipChildren(@Nonnull FileObject asFileObject, @Nonnull ZipFile asZipFile) {
        Enumeration<? extends ZipEntry> entriesEnumeration = asZipFile.entries();

        Map<String, ZipEntry> entriesByPaths = new HashMap<>(); //TODO use LinkedHashMap to save contents order?
        while (entriesEnumeration.hasMoreElements()) {
            ZipEntry entry = entriesEnumeration.nextElement();
            String name = entry.getName();
            if (!name.equals(ZIP_PATH_SEPARATOR)) {
                entriesByPaths.put(name, entry);
            }
        }

        return processEntries(entriesByPaths, asFileObject);
    }

    private static List<FileObject> processEntries(Map<String, ZipEntry> entriesByPaths, FileObject parentArchive) {
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

                    FileObject fileObject = new ZippedFileObject(
                            parentArchive, path, entry.getValue().isDirectory(), parent);
                    fileObjectsByPaths.put(VfsUtils.normalizePath(path, ZIP_PATH_SEPARATOR), fileObject);
                }
            }

            currentNestingLevel++;
        }

        return new ArrayList<>(fileObjectsByPaths.values());
    }

    static int getNestingLevel(@Nonnull String path) {
        path = VfsUtils.normalizePath(path, ZIP_PATH_SEPARATOR);
        return StringUtils.countMatches(path, ZIP_PATH_SEPARATOR);
    }
}
