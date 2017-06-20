package com.pankratyev.jetbrains.filebrowser.ui.files;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;

import javax.swing.JList;
import javax.swing.ListModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class FileListKeySearchListener extends KeyAdapter {
    @SuppressWarnings("FieldCanBeLocal")
    private static int SAME_SEARCH_INTERVAL = 500; // millis

    private String currentSearch;
    private long lastSearchTime;

    @SuppressWarnings("unchecked") // this listener is to be used with JList<FileObject>
    @Override
    public void keyPressed(KeyEvent e) {
        JList<FileObject> fileList = (JList<FileObject>) e.getSource();

        char typedChar = e.getKeyChar();
        if (!isSearchableChar(typedChar)) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime > lastSearchTime + SAME_SEARCH_INTERVAL) {
            currentSearch = "";
        }

        lastSearchTime = currentTime;
        currentSearch += Character.toLowerCase(typedChar);

        ListModel<FileObject> listModel = fileList.getModel();

        int selectedIndex = fileList.getSelectedIndex();
        if (selectedIndex >= 0) {
            FileObject selectedFileObject = listModel.getElementAt(selectedIndex);
            String searchableText = getSearchableText(selectedFileObject);
            if (searchableText.startsWith(currentSearch)) {
                // no need to do anything, currently selected element is fine
                return;
            }
        }

        boolean wasNoSelection = selectedIndex < 0;
        boolean found = false;

        for (int i = wasNoSelection ? 0 : selectedIndex; i < listModel.getSize(); i++) {
            boolean selected = selectIfSuitable(fileList, listModel, i);
            if (selected) {
                found = true;
                break;
            }
        }
        if (found || wasNoSelection) {
            return;
        }

        for (int i = 0; i < selectedIndex; i++) {
            if (selectIfSuitable(fileList, listModel, i)) {
                break;
            }
        }
    }

    private boolean selectIfSuitable(JList<FileObject> fileList, ListModel<FileObject> listModel, int index) {
        FileObject fileObject = listModel.getElementAt(index);
        String searchableText = getSearchableText(fileObject);
        if (searchableText.startsWith(currentSearch)) {
            fileList.setSelectedIndex(index);
            fileList.ensureIndexIsVisible(index);
            return true;
        }
        return false;
    }

    private static String getSearchableText(FileObject fileObject) {
        return FileListCellRenderer.getDisplayText(fileObject).toLowerCase();
    }

    private static boolean isSearchableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
}
