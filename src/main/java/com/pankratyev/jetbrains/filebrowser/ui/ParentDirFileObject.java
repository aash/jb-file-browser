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
 * This wrapper uses a child {@link FileObject}, not a directly parent one, because some {@link FileObject#getParent()}
 * implementations may be lazy.
 *
 * @see FileListCellRenderer
 */
final class ParentDirFileObject implements FileObject {
    private final FileObject child;

    private ParentDirFileObject(FileObject child) {
        this.child = child;
    }

    static ParentDirFileObject createFor(@Nonnull FileObject currentFileObject) {
        if (!Objects.requireNonNull(currentFileObject).hasParent()) {
            throw new IllegalArgumentException("File object doesn't have a parent: " + currentFileObject);
        }
        return new ParentDirFileObject(currentFileObject);
    }


    @Nonnull
    @Override
    public String getName() {
        return "..";
    }

    @Nonnull
    @Override
    public String getFullName() {
        return child.getParent().getFullName();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean hasParent() {
        FileObject parent = child.getParent();
        return parent != null && parent.hasParent();
    }

    @Nullable
    @Override
    public FileObject getParent() {
        FileObject parent = child.getParent();
        if (parent != null) {
            return parent.getParent();
        }
        return null;
    }

    @Nullable
    @Override
    public List<FileObject> getChildren() throws IOException {
        return child.getParent().getChildren();
    }

    @Nullable
    @Override
    public InputStream getInputStream() throws IOException {
        return child.getParent().getInputStream();
    }

    @Override
    public boolean isZipArchive() {
        return child.getParent().isZipArchive();
    }

    @Nonnull
    @Override
    public ZipFile toZipFile() throws IOException {
        return child.getParent().toZipFile();
    }

    @Override
    public int compareTo(@Nonnull FileObject ignore) {
        return -1; // this FileObject implementation should always be displayed at the very top of list in UI
    }

    @Override
    public String toString() {
        FileObject realObject = child.getParent();
        return "PDFO{" +
                (realObject == null ? ("child=" + child) : realObject) +
                '}';
    }
}
