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
import java.util.List;

/**
 * File browser UI.
 * @see FileBrowserController
 */
public final class FileBrowser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileBrowser.class);

    private final FileTypeProvider fileTypeProvider;
    private final UserDirectoriesProvider userDirectoriesProvider;
    private final FileBrowserController controller;

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

        this.controller = new FileBrowserController(this);

        fileList.addMouseListener(new FileListDoubleClickListener(controller));
    }

    /**
     * Sets current directory contents displayed in file list.
     * @param contents {@link FileObject} list to be displayed; usually the first element in list should be a
     * {@link ParentDirFileObject}.
     * @throws IOException on I/O errors while trying to read directory children.
     */
    public void setCurrentDirectoryContents(List<FileObject> contents) throws IOException {
        fileListModel.clear();
        for (FileObject fileObject : contents) {
            fileListModel.addElement(fileObject);
        }
    }

    public void clearPreview() {
        previewPanel.removeAll();
        previewPanel.revalidate();
        previewPanel.repaint();
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public FileBrowserController getController() {
        return controller;
    }
}
