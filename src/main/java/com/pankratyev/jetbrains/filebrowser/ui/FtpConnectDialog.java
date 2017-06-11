package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.ftp.FtpClientFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

public class FtpConnectDialog extends JDialog {
    private static final int DEFAULT_FTP_PORT = 21;

    private final FileBrowserController controller;

    private FTPClient createdClient = null;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField hostField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField portField;
    private JLabel errorMessageLabel;

    public FtpConnectDialog(@Nonnull FileBrowserController controller) {
        this.controller = Objects.requireNonNull(controller);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        String host = hostField.getText();
        int port;
        try {
            if (StringUtils.isNotBlank(portField.getText())) {
                port = Integer.parseInt(portField.getText());
            } else {
                port = DEFAULT_FTP_PORT;
            }
        } catch (NumberFormatException e) {
            errorMessageLabel.setText("Invalid port number");
            pack();
            return;
        }
        String username = usernameField.getText();

        @SuppressWarnings("deprecation") // FTPClient.login() requires password as a String anyway
        String password = passwordField.getText();

        try {
            createdClient = FtpClientFactory.createClient(host, port, username, password);
        } catch (IOException e) {
            errorMessageLabel.setText("Unable to connect: " + e.getMessage());
            pack();
            return;
        }

        dispose();
    }

    private void onCancel() {
        dispose();
    }

    /**
     * @return null if dialog was closed not with OK button or if connection couldn't be established; initialized FTP
     * client if dialog was closed with OK button and connection was established.
     */
    @Nullable
    public FTPClient getFtpClient() {
        return createdClient;
    }
}
