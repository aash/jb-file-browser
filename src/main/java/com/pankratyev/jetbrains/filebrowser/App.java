package com.pankratyev.jetbrains.filebrowser;

import com.pankratyev.jetbrains.filebrowser.ui.Browser;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.ExtensionBasedFileTypeProvider;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Entry point.
 */
public final class App {
    private static final String WINDOW_TITLE = "File browser with preview";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 600;

    private static final String MENU_FILE = "File";
    private static final String MENU_ITEM_FTP_CONNECT = "Connect to FTP server";
    private static final String MENU_ITEM_FTP_DISCONNECT = "Disconnect from FTP server";


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException,
            InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createFrame().setVisible(true);
            }
        });
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setContentPane(new Browser(new ExtensionBasedFileTypeProvider()).getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setJMenuBar(createMenuBar());
        return frame;
    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(MENU_FILE);
        JMenuItem ftpConnectItem = new JMenuItem(MENU_ITEM_FTP_CONNECT);
        ftpConnectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO implement
            }
        });
        fileMenu.add(ftpConnectItem);
        JMenuItem ftpDisconnectItem = new JMenuItem(MENU_ITEM_FTP_DISCONNECT);
        ftpDisconnectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO implement
            }
        });
        ftpDisconnectItem.setEnabled(false); // on application start there's no FTP connection
        fileMenu.add(ftpDisconnectItem);
        menuBar.add(fileMenu);
        return menuBar;
    }
}
