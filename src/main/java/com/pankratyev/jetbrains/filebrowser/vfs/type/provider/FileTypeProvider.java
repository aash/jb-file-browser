package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.UnknownFileType;

import javax.annotation.Nonnull;
import java.io.IOException;

public interface FileTypeProvider {
    /**
     * Determines the type of file.
     * Some implementations may read the file content to determine it's type; but they shouldn't throw
     * {@link IOException} on any I/O errors; instead they should return {@link UnknownFileType} instance.
     */
    @Nonnull
    FileType getType(FileObject file);
}
