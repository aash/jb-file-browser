package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.BmpImageFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.DirectoryFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.GifImageFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.JpegImageFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.PngImageFileType;
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
    private static final char EXTENSION_PREFIX = '.';
    private static final Map<String, FileType> FILE_TYPES;
    static {
        FILE_TYPES = new HashMap<>();
        FILE_TYPES.put("png", new PngImageFileType());
        FILE_TYPES.put("bmp", new BmpImageFileType());
        FILE_TYPES.put("gif", new GifImageFileType());
        JpegImageFileType jpegType = new JpegImageFileType();
        FILE_TYPES.put("jpeg", jpegType);
        FILE_TYPES.put("jpg", jpegType);
        TextFileType textType = new TextFileType();
        FILE_TYPES.put("txt", textType);
        FILE_TYPES.put("log", textType);
        FILE_TYPES.put("htm", textType);
        FILE_TYPES.put("html", textType);
        FILE_TYPES.put("xml", textType);
        FILE_TYPES.put("json", textType);
        FILE_TYPES.put("sh", textType);
        FILE_TYPES.put("bat", textType);
        FILE_TYPES.put("ini", textType);
        FILE_TYPES.put("iml", textType);
        FILE_TYPES.put("js", textType);
        FILE_TYPES.put("md", textType);
    }

    @Nonnull
    @Override
    public FileType getType(FileObject file) {
        if (file.isDirectory()) {
            return new DirectoryFileType();
        }
        String extension = FilenameUtils.getExtension(file.getName());
        if (extension.isEmpty()) {
            return new UnknownFileType();
        }
        FileType type = FILE_TYPES.get(extension);
        return type != null ? type : new UnknownFileType();
    }
}
