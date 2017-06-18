package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.ui.files.FileListCellRenderer;
import com.pankratyev.jetbrains.filebrowser.ui.files.FileListDoubleClickListener;
import com.pankratyev.jetbrains.filebrowser.ui.files.FileListEnterAction;
import com.pankratyev.jetbrains.filebrowser.ui.files.FileListSelectionListener;
import com.pankratyev.jetbrains.filebrowser.ui.userdir.UserDirectoryLink;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;

import javax.annotation.Nonnull;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * File browser UI.
 * @see FileBrowserController
 */
public final class FileBrowser {
    private static final String ERROR_DIALOG_TITLE = "Error";

    private final FileBrowserController controller;

    private final DefaultListModel<FileObject> fileListModel = new DefaultListModel<>();

    private boolean ftpMode = false;
    private String ftpPathPrefix = null;


    private JPanel mainPanel;
    private JTextField pathField;
    private JList<FileObject> fileList;
    private JPanel previewPanel;
    private JPanel userDirectoriesPanel;
    private JSeparator userDirectoriesSeparator;
    private JSplitPane splitPane;
    private JPanel previewHelpPanel;

    @SuppressWarnings("unchecked")
    public FileBrowser(@Nonnull FileTypeProvider fileTypeProvider,
            @Nonnull UserDirectoriesProvider userDirectoriesProvider,
            @Nonnull FileObject initialFileObject) {
        this.controller = new FileBrowserController(this, fileTypeProvider, initialFileObject);
        setupFileList(fileTypeProvider);
        addUserDirectories(userDirectoriesProvider.getUserDirectories());
        setupSplitPane();
    }

    private void setupFileList(FileTypeProvider fileTypeProvider) {
        fileList.setModel(fileListModel);
        fileList.setCellRenderer(new FileListCellRenderer(fileTypeProvider));

        fileList.addMouseListener(new FileListDoubleClickListener(controller));
        fileList.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "changeDir");
        fileList.getActionMap().put("changeDir", new FileListEnterAction(controller));

        fileList.addListSelectionListener(new FileListSelectionListener(controller));
    }

    private void addUserDirectories(Collection<String> userDirectories) {
        userDirectoriesPanel.setLayout(new BoxLayout(userDirectoriesPanel, BoxLayout.Y_AXIS));

        for (String userDirectory : userDirectories) {
            UserDirectoryLink userDirLabel = new UserDirectoryLink(userDirectory, controller);
            userDirLabel.setIcon(IconRegistry.FOLDER);
            JPanel labelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
            labelWrapper.add(userDirLabel);
            userDirectoriesPanel.add(labelWrapper);
        }
    }

    private void setupSplitPane() {
        splitPane.setBorder(null);
    }


    /**
     * Sets current directory contents displayed in file list.
     * @param contents {@link FileObject} list to be displayed; usually the first element in list should be a
     * {@link ParentDirFileObject}. This list can be sorted.
     */
    void setCurrentDirectoryContents(@Nonnull List<FileObject> contents) {
        fileListModel.clear();
        Collections.sort(contents, FILE_OBJECT_COMPARATOR);
        for (FileObject fileObject : contents) {
            fileListModel.addElement(fileObject);
        }
    }

    void setCurrentPath(@Nonnull String path) {
        if (isFtpMode()) {
            path = ftpPathPrefix + path;
        }
        pathField.setText(path);
    }

    void setSelectedFileObject(@Nonnull FileObject toSelect) {
        fileList.setSelectedValue(toSelect, true);
    }

    /**
     * Clears the preview panel.
     */
    void clearPreview() {
        previewPanel.removeAll();
        previewPanel.add(previewHelpPanel);
        redrawPreview();
    }

    /**
     * Shows the preloader on the preview panel.
     */
    void showPreloader() {
        previewPanel.removeAll();
        previewPanel.add(new JLabel(IconRegistry.PRELOADER));
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

    @Nonnull
    Dimension getPreviewPanelSize() {
        return previewPanel.getSize();
    }

    void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(getMainPanel().getParent(), message,
                ERROR_DIALOG_TITLE, JOptionPane.ERROR_MESSAGE);
    }

    void enableFtpMode(String ftpPathPrefix) {
        ftpMode = true;
        this.ftpPathPrefix = ftpPathPrefix;
        hideUserDirectories();
    }

    void disableFtpMode() {
        ftpMode = false;
        ftpPathPrefix = null;
        showUserDirectories();
    }

    boolean isFtpMode() {
        return ftpMode;
    }

    private void hideUserDirectories() {
        userDirectoriesPanel.setVisible(false);
        userDirectoriesSeparator.setVisible(false);
    }

    private void showUserDirectories() {
        userDirectoriesPanel.setVisible(true);
        userDirectoriesSeparator.setVisible(true);
    }


    @Nonnull
    public JPanel getMainPanel() {
        return mainPanel;
    }

    @Nonnull
    public FileBrowserController getController() {
        return controller;
    }


    private static final Comparator<FileObject> FILE_OBJECT_COMPARATOR = new Comparator<FileObject>() {
        @Override
        public int compare(FileObject o1, FileObject o2) {
            if (o1 instanceof ParentDirFileObject) {
                return -1;
            }
            if (o2 instanceof ParentDirFileObject) {
                return 1;
            }

            if (o1.isDirectory() && !o2.isDirectory()) {
                return -1;
            }
            if (o2.isDirectory() && !o1.isDirectory()) {
                return 1;
            }

            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
}
