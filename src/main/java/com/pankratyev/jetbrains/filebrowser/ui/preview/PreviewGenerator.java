package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.io.IOException;

public interface PreviewGenerator {
    /**
     * Generates preview for passed {@link FileObject}. Concrete implementations may have different ways to obtain
     * the preview but usually it includes reading file content for supported file types (except
     * directories and archives).
     *
     * This method doesn't throw {@link IOException} if file content can't be read; in any exceptional situations
     * it will return a "broken file" preview.
     *
     * @param fileObject file to generate preview for.
     * @param maxWidth required preview component max width (in px).
     * @param maxHeight required preview component max height (in px).
     * @return preview (usually it is a {@link JLabel} with image).
     */
    @Nonnull
    JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight);
}
