package com.pankratyev.jetbrains.filebrowser.ui.filetype.provider;

import com.pankratyev.jetbrains.filebrowser.ui.filetype.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;

public interface FileTypeProvider {
    /**
     * Determines the type of file.
     */
    @Nonnull
    FileType getType(FileObject file);
}
