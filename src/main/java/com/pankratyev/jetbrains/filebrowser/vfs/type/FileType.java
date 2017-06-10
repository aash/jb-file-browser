package com.pankratyev.jetbrains.filebrowser.vfs.type;

import com.pankratyev.jetbrains.filebrowser.ui.preview.PreviewGenerator;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.Icon;

/**
 * Represents a file type.
 * Concrete file types are used in order to determine which icon and which {@link PreviewGenerator} implementation
 * should be used for some {@link FileObject}.
 * This is basically a bridge between VFS-related things and UI.
 */
public interface FileType {
    /**
     * @return icon for this file type.
     */
    @Nonnull
    Icon getIcon();

    /**
     * @return preview generator for this file type.
     */
    @Nonnull
    PreviewGenerator getPreviewGenerator();
}
