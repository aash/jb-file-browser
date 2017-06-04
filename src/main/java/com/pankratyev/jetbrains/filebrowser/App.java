package com.pankratyev.jetbrains.filebrowser;

import com.pankratyev.jetbrains.filebrowser.ui.Browser;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObjectFactory;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProviderFactory;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.ExtensionBasedFileTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.io.IOException;

/**
 * Entry point.
 */
public final class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private static final String WINDOW_TITLE = "File browser with preview";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 600;

    private static final String MENU_FILE = "File";
    private static final String MENU_ITEM_FTP_CONNECT = "Connect to FTP server";
    private static final String MENU_ITEM_FTP_DISCONNECT = "Disconnect from FTP server";


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException,
            InstantiationException, IllegalAccessException {
        LOGGER.info("Application start");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    createAndInitFrame().setVisible(true);
                } catch (IOException e) {
                    //TODO what is the best way to handle this?
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static JFrame createAndInitFrame() throws IOException {
        JFrame frame = new JFrame(WINDOW_TITLE);

        ExtensionBasedFileTypeProvider fileTypeProvider = getFileTypeProvider();
        UserDirectoriesProvider userDirProvider = getUserDirectoriesProvider();
        FileObject initialFileObject = getInitialFileObject(userDirProvider);

        Browser browser = new Browser(fileTypeProvider, userDirProvider);
        browser.setCurrentDirectory(initialFileObject);

        frame.setContentPane(browser.getMainPanel());

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

    private static ExtensionBasedFileTypeProvider getFileTypeProvider() {
        return new ExtensionBasedFileTypeProvider();
    }

    private static UserDirectoriesProvider getUserDirectoriesProvider() {
        return UserDirectoriesProviderFactory.getUserDirectoriesProvider();
    }

    private static FileObject getInitialFileObject(UserDirectoriesProvider userDirectoriesProvider) {
        return LocalFileObjectFactory.create(userDirectoriesProvider.getHomeDirectory());
    }
}
