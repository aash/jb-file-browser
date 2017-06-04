package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;
import java.util.Objects;

/**
 * @see com.pankratyev.jetbrains.filebrowser.ui.ParentDirFileObject
 */
public final class FileListCellRenderer extends DefaultListCellRenderer {
    private final FileTypeProvider fileTypeProvider;

    public FileListCellRenderer(FileTypeProvider provider) {
        fileTypeProvider = Objects.requireNonNull(provider);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        FileObject file = (FileObject) value;
        FileType type = fileTypeProvider.getType(file);
        setIcon(type.getIcon());
        setText(file.getName());
        return this;
    }
}
