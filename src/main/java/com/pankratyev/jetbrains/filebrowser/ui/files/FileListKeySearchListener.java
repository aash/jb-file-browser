package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.swing.JList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class FileListKeySearchListener extends KeyAdapter {
    @SuppressWarnings("FieldCanBeLocal")
    private static int SAME_SEARCH_INTERVAL = 1000; // millis
    private String currentSearch;
    private long lastSearchTime;

    @SuppressWarnings("unchecked") // this listener is to be used with JList<FileObject>
    @Override
    public void keyPressed(KeyEvent e) {
        JList<FileObject> fileList = (JList<FileObject>) e.getSource();

        char typedChar = e.getKeyChar();
        if (!Character.isLetterOrDigit(typedChar)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime > lastSearchTime + SAME_SEARCH_INTERVAL) {
            currentSearch = "";
        }

        lastSearchTime = currentTime;
        currentSearch += Character.toLowerCase(typedChar);

        // can be speed up, currently it is linear search
        for (int i = 0; i < fileList.getModel().getSize(); i++) {
            FileObject fileObject = fileList.getModel().getElementAt(i);
            String displayText = FileListCellRenderer.getDisplayText(fileObject).toLowerCase();
            if (displayText.startsWith(currentSearch)) {
                fileList.setSelectedIndex(i);
                fileList.ensureIndexIsVisible(i);
                break;
            }
        }
    }
}
