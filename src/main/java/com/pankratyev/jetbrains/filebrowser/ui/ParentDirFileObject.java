package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.ui.files.FileListCellRenderer;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipFile;

/**
 * Wrapper used only to display parent directory for some {@link FileObject} as ".." in UI.
 *
 * @see FileListCellRenderer
 */
final class ParentDirFileObject implements FileObject {
    private final FileObject realFileObject;

    private ParentDirFileObject(FileObject object) {
        realFileObject = Objects.requireNonNull(object);
    }

    static ParentDirFileObject wrap(@Nonnull FileObject fileObject) {
        return new ParentDirFileObject(fileObject);
    }


    @Nonnull
    @Override
    public String getName() {
        return "..";
    }

    @Nonnull
    @Override
    public String getFullName() {
        return realFileObject.getFullName();
    }

    @Override
    public boolean isDirectory() {
        return realFileObject.isDirectory();
    }

    @Nullable
    @Override
    public FileObject getParent() {
        return realFileObject.getParent();
    }

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        return realFileObject.getChildren();
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        return realFileObject.getInputStream();
    }

    @Override
    public boolean isZipArchive() {
        return realFileObject.isZipArchive();
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        return realFileObject.toZipFile();
    }

    @Override
    public int compareTo(@Nonnull FileObject ignore) {
        return -1; // this FileObject implementation should always be displayed at the very top of list in UI
    }
}
