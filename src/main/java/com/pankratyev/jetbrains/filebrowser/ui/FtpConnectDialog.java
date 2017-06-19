package com.pankratyev.jetbrains.filebrowser.ui;

import com.pankratyev.jetbrains.filebrowser.vfs.ftp.FtpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class FtpConnectDialog extends JDialog {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpConnectDialog.class);

    private static final int DEFAULT_FTP_PORT = 21;
    private static final String FTP_PREFIX = "ftp://";

    private volatile FtpClient createdClient = null;

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField hostField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField portField;
    private JLabel errorMessageLabel;

    public FtpConnectDialog() {
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
        if (host.startsWith(FTP_PREFIX)) {
            host = host.substring(FTP_PREFIX.length());
        }

        if (host.isEmpty()) {
            errorMessageLabel.setText("Invalid host");
            pack();
            return;
        }

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

        createdClient = new FtpClient(host, port, username, password);

        errorMessageLabel.setIcon(IconRegistry.PRELOADER_SMALL);
        pack();

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                try {
                    createdClient.testConnection();
                    return null;
                } catch (IOException e) {
                    createdClient = null;
                    return e.getMessage();
                }
            }

            @Override
            protected void done() {
                errorMessageLabel.setIcon(null);

                try {
                    String errorMessage = get();
                    if (errorMessage == null) {
                        dispose();
                    } else {
                        errorMessageLabel.setText("Unable to connect: " + errorMessage);
                        pack();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    LOGGER.warn("Cannot perform FTP connection check", e);
                    errorMessageLabel.setText("Cannot perform FTP connection check: " + e.getMessage());
                    pack();
                }
            }
        }.execute();
    }

    private void onCancel() {
        dispose();
    }

    /**
     * @return null if dialog was closed not with OK button or if connection couldn't be established; initialized FTP
     * client if dialog was closed with OK button and connection was established.
     */
    @Nullable
    public FtpClient getFtpClient() {
        return createdClient;
    }
}
