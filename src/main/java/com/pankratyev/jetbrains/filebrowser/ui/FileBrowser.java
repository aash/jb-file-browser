package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.ui.files.FileListCellRenderer;
import com.pankratyev.jetbrains.filebrowser.ui.files.FileListDoubleClickListener;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.io.IOException;
import java.util.Collection;

public final class FileBrowser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileBrowser.class);

    private final FileTypeProvider fileTypeProvider;
    private final UserDirectoriesProvider userDirectoriesProvider;

    private final DefaultListModel<FileObject> fileListModel = new DefaultListModel<>();

    private JPanel mainPanel;
    private JTextField pathField;
    private JList<FileObject> fileList;
    private JPanel previewPanel;
    private JPanel userDirectoriesPanel;
    private JPanel navigationPanel;

    @SuppressWarnings("unchecked")
    public FileBrowser(FileTypeProvider fileTypeProvider, UserDirectoriesProvider userDirectoriesProvider) {
        this.fileTypeProvider = fileTypeProvider;
        this.userDirectoriesProvider = userDirectoriesProvider;

        fileList.setModel(fileListModel);
        fileList.setCellRenderer(new FileListCellRenderer(fileTypeProvider));
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fileList.addMouseListener(new FileListDoubleClickListener(this));
    }

    /**
     * Sets current directory displayed in file list.
     * @param dir current directory; its children and parent will be displayed.
     * @throws IOException on I/O errors while trying to read directory children.
     */
    public void setCurrentDirectory(FileObject dir) throws IOException {
        LOGGER.debug("Changing directory to " + dir);

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }

        fileListModel.clear();

        FileObject parent = dir.getParent();
        if (parent != null) {
            fileListModel.addElement(ParentDirFileObject.wrap(parent));
        }

        Collection<FileObject> children = dir.getChildren();
        if (children != null) {
            for (FileObject child : children) {
                fileListModel.addElement(child);
            }
        }

        LOGGER.debug("Directory changed to " + dir);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
