package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

public interface PreviewGenerator {
    /**
     * Generates preview for passed {@link FileObject}. Concrete implementations may have different ways to obtain
     * the preview but usually it includes reading file content (not applicable to directories).
     *
     * @param fileObject file to generate preview for.
     * @return preview; client may use {@link Image#getScaledInstance(int, int, int)} to get the scaled image.
     * @throws IOException on I/O errors while reading the file content.
     */
    @Nonnull
    BufferedImage generatePreview(FileObject fileObject) throws IOException;
}
