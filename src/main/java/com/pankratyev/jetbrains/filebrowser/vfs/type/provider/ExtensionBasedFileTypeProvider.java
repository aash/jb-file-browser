package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.ArchiveFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.DirectoryFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.ImageFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.TextFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.UnknownFileType;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@link FileTypeProvider}.
 * Determines file type by extension.
 */
public final class ExtensionBasedFileTypeProvider implements FileTypeProvider {
    private static final ImageFileType IMAGE_TYPE = new ImageFileType();
    private static final TextFileType TEXT_TYPE = new TextFileType();
    private static final ArchiveFileType ARCHIVE_TYPE = new ArchiveFileType();
    private static final UnknownFileType UNKNOWN_TYPE = new UnknownFileType();

    private static final Map<String, FileType> FILE_TYPES = new HashMap<>();
    static {
        FILE_TYPES.put("jpeg", IMAGE_TYPE);
        FILE_TYPES.put("jpg", IMAGE_TYPE);
        FILE_TYPES.put("png", IMAGE_TYPE);
        FILE_TYPES.put("bmp", IMAGE_TYPE);
        FILE_TYPES.put("gif", IMAGE_TYPE);
        FILE_TYPES.put("txt", TEXT_TYPE);
        FILE_TYPES.put("log", TEXT_TYPE);
        FILE_TYPES.put("csv", TEXT_TYPE);
        FILE_TYPES.put("htm", TEXT_TYPE);
        FILE_TYPES.put("html", TEXT_TYPE);
        FILE_TYPES.put("xml", TEXT_TYPE);
        FILE_TYPES.put("xhtml", TEXT_TYPE);
        FILE_TYPES.put("css", TEXT_TYPE);
        FILE_TYPES.put("json", TEXT_TYPE);
        FILE_TYPES.put("sh", TEXT_TYPE);
        FILE_TYPES.put("bat", TEXT_TYPE);
        FILE_TYPES.put("ini", TEXT_TYPE);
        FILE_TYPES.put("iml", TEXT_TYPE);
        FILE_TYPES.put("js", TEXT_TYPE);
        FILE_TYPES.put("java", TEXT_TYPE);
        FILE_TYPES.put("cpp", TEXT_TYPE);
        FILE_TYPES.put("jsp", TEXT_TYPE);
        FILE_TYPES.put("php", TEXT_TYPE);
        FILE_TYPES.put("py", TEXT_TYPE);
        FILE_TYPES.put("md", TEXT_TYPE);
        FILE_TYPES.put("zip", ARCHIVE_TYPE);
        FILE_TYPES.put("jar", ARCHIVE_TYPE);
        FILE_TYPES.put("war", ARCHIVE_TYPE);
    }

    @Nonnull
    @Override
    public FileType getType(FileObject file) {
        if (file.isDirectory()) {
            return new DirectoryFileType();
        }
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        FileType type = FILE_TYPES.get(extension);
        return type != null ? type : UNKNOWN_TYPE;
    }
}
