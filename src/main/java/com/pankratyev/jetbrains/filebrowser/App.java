package com.pankratyev.jetbrains.filebrowser;

import com.pankratyev.jetbrains.filebrowser.ui.FileBrowser;
import com.pankratyev.jetbrains.filebrowser.ui.FileBrowserController;
import com.pankratyev.jetbrains.filebrowser.ui.FtpConnectDialog;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObjectFactory;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.local.user.UserDirectoriesProviderFactory;
import com.pankratyev.jetbrains.filebrowser.vfs.type.provider.ExtensionBasedFileTypeProvider;
import org.apache.commons.net.ftp.FTPClient;
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                createAndInitFrame().setVisible(true);
            }
        });
    }

    private static JFrame createAndInitFrame() {
        JFrame frame = new JFrame(WINDOW_TITLE);

        ExtensionBasedFileTypeProvider fileTypeProvider = getFileTypeProvider();
        UserDirectoriesProvider userDirProvider = getUserDirectoriesProvider();
        FileObject initialFileObject = getInitialFileObject(userDirProvider);

        FileBrowser browser = new FileBrowser(fileTypeProvider, userDirProvider);
        FileBrowserController browserController = browser.getController();
        browserController.changeDirectory(initialFileObject);

        frame.setContentPane(browser.getMainPanel());
        frame.setJMenuBar(createMenuBar(frame, browserController));

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setMinimumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        frame.setLocationByPlatform(true);

        return frame;
    }

    private static JMenuBar createMenuBar(final JFrame frame, final FileBrowserController controller) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(MENU_FILE);
        JMenuItem ftpConnectItem = new JMenuItem(MENU_ITEM_FTP_CONNECT);
        ftpConnectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FtpConnectDialog dialog = new FtpConnectDialog(controller);
                dialog.pack();
                dialog.setLocationRelativeTo(frame);
                dialog.setVisible(true);

                FTPClient client = dialog.getFtpClient();
                if (client != null) {
                    controller.connectToFtp(client);
                } // else - do nothing, dialog was canceled
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
