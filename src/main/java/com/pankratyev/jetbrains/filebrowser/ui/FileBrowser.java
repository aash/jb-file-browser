package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.ui.files.FileListCellRenderer;
import com.pankratyev.jetbrains.filebrowser.ui.files.FileListDoubleClickListener;
import com.pankratyev.jetbrains.filebrowser.ui.files.FileListSelectionListener;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

/**
 * File browser UI.
 * @see FileBrowserController
 */
public final class FileBrowser {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileBrowser.class);

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
    public FileBrowser(@Nonnull FileTypeProvider fileTypeProvider,
            @Nonnull UserDirectoriesProvider userDirectoriesProvider) {
        this.userDirectoriesProvider = userDirectoriesProvider;

        fileList.setModel(fileListModel);
        fileList.setCellRenderer(new FileListCellRenderer(fileTypeProvider));

        this.controller = new FileBrowserController(this, fileTypeProvider);

        fileList.addMouseListener(new FileListDoubleClickListener(controller));
        fileList.addListSelectionListener(new FileListSelectionListener(controller));
    }

    /**
     * Sets current directory contents displayed in file list.
     * @param contents {@link FileObject} list to be displayed; usually the first element in list should be a
     * {@link ParentDirFileObject}.
     */
    void setCurrentDirectoryContents(@Nonnull List<FileObject> contents) {
        fileListModel.clear();
        for (FileObject fileObject : contents) {
            fileListModel.addElement(fileObject);
        }
    }

    void setCurrentPath(@Nonnull String path) {
        //TODO if (isFtpConnection()) ...
        pathField.setText(path);
    }

    /**
     * Clears the preview panel.
     */
    void clearPreview() {
        previewPanel.removeAll();
        redrawPreview();
    }

    /**
     * Sets the contents of the preview panel.
     * @param preview component with preview to display.
     */
    void setPreview(@Nonnull JComponent preview) {
        previewPanel.removeAll();
        previewPanel.add(preview);
        redrawPreview();
    }

    private void redrawPreview() {
        previewPanel.revalidate();
        previewPanel.repaint();
    }

    Dimension getPreviewPanelSize() {
        return previewPanel.getSize();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public FileBrowserController getController() {
        return controller;
    }
}
