package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.io.IOException;

public interface PreviewGenerator {
    /**
     * Generates preview for passed {@link FileObject}. Concrete implementations may have different ways to obtain
     * the preview but usually it includes reading file content (not applicable to directories).
     *
     * @param fileObject file to generate preview for.
     * @param maxWidth required preview component max width (in px).
     * @param maxHeight required preview component max height (in px).
     * @return preview (usually it is a {@link JLabel} with image).
     * @throws IOException on I/O errors while reading the file content.
     */
    @Nonnull
    JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) throws IOException;
}
