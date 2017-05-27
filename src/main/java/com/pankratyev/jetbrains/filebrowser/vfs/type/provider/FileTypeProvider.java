package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;

import javax.annotation.Nonnull;

public interface FileTypeProvider {
    /**
     * Determines the type of file.
     */
    @Nonnull
    FileType getType(FileObject file);
}
