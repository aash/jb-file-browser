package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.FileType;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.provider.FileTypeProvider;

import javax.annotation.Nonnull;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;
import java.util.Objects;

/**
 * @see com.pankratyev.jetbrains.filebrowser.ui.ParentDirFileObject
 */
public final class FileListCellRenderer extends DefaultListCellRenderer {
    private final FileTypeProvider fileTypeProvider;

    public FileListCellRenderer(@Nonnull FileTypeProvider provider) {
        fileTypeProvider = Objects.requireNonNull(provider);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        FileObject fileObject = (FileObject) value;
        FileType type = fileTypeProvider.getType(fileObject);
        setIcon(type.getIcon());
        setText(getDisplayText(fileObject));
        return this;
    }

    @Nonnull
    static String getDisplayText(FileObject fileObject) {
        return fileObject.getName();
    }
}
