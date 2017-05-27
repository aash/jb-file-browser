package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.FileTypeProvider;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 */
public final class Browser {
    private final FileTypeProvider fileTypeProvider;

    private JPanel mainPanel;
    private JTextField pathField;
    private JList fileList;
    private JPanel previewPanel;
    private JPanel fileInfoPanel;
    private JPanel navigationPanel;
    private JTable fileInfoTable;

    public Browser(FileTypeProvider fileTypeProvider) {
        this.fileTypeProvider = fileTypeProvider;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
