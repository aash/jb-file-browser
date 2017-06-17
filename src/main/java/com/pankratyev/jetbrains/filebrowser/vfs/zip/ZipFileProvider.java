package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Provides a way to obtain a {@link ZipFile instance} for archive stored locally or on FTP server.
 * {@link ZipFile} is used because it is more efficient for navigating inside archives than {@link ZipInputStream}.
 */
interface ZipFileProvider {
    /**
     * Provides a {@link ZipFile} instance.
     * @throws IllegalStateException if this {@link FileObject} is not a zip archive.
     * @throws ZipException if a ZIP format error has occurred.
     * @throws IOException if an I/O error has occurred.
     * @return {@link ZipFile} for current {@link FileObject}.
     */
    @Nonnull
    ZipFile getZipFile() throws IOException;
}
