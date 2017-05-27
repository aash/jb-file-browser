package com.pankratyev.jetbrains.filebrowser.ui;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 */
public final class Browser {
    private JPanel mainPanel;
    private JTextField pathField;
    private JList fileList;
    private JPanel previewPanel;
    private JPanel fileInfoPanel;
    private JPanel navigationPanel;
    private JTable fileInfoTable;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
