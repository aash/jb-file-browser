package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JComponent;

public interface PreviewGenerator {
    /**
     * Generates preview for passed {@link FileObject}. Concrete implementations may have different ways to obtain
     * the preview but usually it includes reading file content (not applicable to directories).
     *
     * @param fileObject file to generate preview for.
     * @return preview.
     */
    @Nonnull
    JComponent generatePreview(FileObject fileObject); //TODO is JComponent the best choice here?
}
