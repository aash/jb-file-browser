package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * FTP client with {@link FileObject}-based API.
 * None of its methods should be called from EDT (obviously).
 */
@ThreadSafe
public final class FtpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpClient.class);

    private static final String FTP_DEFAULT_PATH = "/";
    private static final String FTP_PATH_SEPARATOR = "/";
    private static final String FTP_DEFAULT_USERNAME = "anonymous";
    private static final String FTP_DEFAULT_PASSWORD = "anonymous";
    private static final int FTP_TIMEOUT = 30 * 1000; // millis

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private final LocalCopyManager localCopyManager;

    public FtpClient(@Nonnull String host, int port, @Nullable String username, @Nullable String password) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.username = username;
        this.password = password;
        this.localCopyManager = new LocalCopyManager(host);
    }


    public void testConnection() throws IOException {
        FTPClient client = createClient();
        try {
            sendNoOp(client);
        } finally {
            disconnect(client);
        }
    }

    @Nonnull
    public FileObject getInitialDirectory() throws IOException {
        FTPClient client = createClient();
        try {
            return getCurrentDirectory(client);
        } finally {
            disconnect(client);
        }
    }


    private FileObject getCurrentDirectory(FTPClient client) throws IOException {
        String currentDir = client.printWorkingDirectory();
        if (currentDir == null) {
            // can happen with some FTP servers
            currentDir = FTP_DEFAULT_PATH;
        }
        return new FtpFileObject(this, currentDir, null, true, localCopyManager);
    }


    @Nonnull
    List<FileObject> list(@Nonnull FtpFileObject directory) throws IOException {
        FTPClient client = createClient();
        try {
            String dirPath = directory.getFullName();

            client.changeWorkingDirectory(dirPath);
            FTPFile[] files = client.listFiles();

            List<FileObject> children = new ArrayList<>();
            for (FTPFile file : files) {
                String filePath = (dirPath.endsWith(FTP_PATH_SEPARATOR) ? "" : dirPath)
                        + FTP_PATH_SEPARATOR + file.getName();
                children.add(new FtpFileObject(this, filePath, directory, file.isDirectory(), localCopyManager));
            }

            return children;
        } finally {
            disconnect(client);
        }
    }

    @Nullable
    FileObject getParentDirectory(@Nonnull FtpFileObject fileObject) throws IOException {
        FTPClient client = createClient();
        try {
            client.changeWorkingDirectory(fileObject.getFullName());
            boolean changedToParent = client.changeToParentDirectory();
            if (!changedToParent) {
                return null;
            }

            FileObject parent = getCurrentDirectory(client);
            boolean changedBack = client.changeWorkingDirectory(fileObject.getFullName());
            if (!changedBack) {
                disconnect(client);
                throw new IOException("Directory changed to parent, cannot change back");
            }

            return parent;
        } finally {
            disconnect(client);
        }
    }

    void retrieveFile(@Nonnull FtpFileObject file, @Nonnull OutputStream to) throws IOException {
        String pathToRetrieve = file.getFullName();
        FTPClient client = createClient();
        try {
            // note: this method is used instead of retrieveFileStream because
            // it doesn't require keeping the control connection alive
            boolean completed = client.retrieveFile(pathToRetrieve, to);
            if (!completed) {
                throw new IOException("Cannot complete file transfer: " + pathToRetrieve);
            }
        } finally {
            disconnect(client);
        }
    }


    private void sendNoOp(FTPClient client) throws IOException {
        boolean answer = client.sendNoOp();
        if (!answer) {
            throw new IOException("NOOP command was unsuccessful");
        }
    }


    @Nonnull
    private FTPClient createClient() throws IOException {
        LOGGER.debug("Creating a new FTP client for {}", host);

        FTPClient client = new FTPClient();
        client.setConnectTimeout(FTP_TIMEOUT);
        client.connect(host, port);
        client.enterLocalPassiveMode();

        try {
            boolean loggedIn = client.login(
                    StringUtils.isNotEmpty(username) ? username : FTP_DEFAULT_USERNAME,
                    StringUtils.isNotEmpty(password) ? password : FTP_DEFAULT_PASSWORD);
            if (!loggedIn) {
                throw new IOException("Login failed; wrong credentials?");
            }

            client.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            disconnect(client);
            throw e;
        }

        return client;
    }

    private void disconnect(FTPClient client) {
        try {
            client.disconnect();
            LOGGER.debug("Disconnected from {}", host);
        } catch (IOException e) {
            LOGGER.warn("An error occurred while trying to disconnect from " + host, e);
        }
    }


    /**
     * @return current FTP URL including username and port, trailing slash is absent.
     */
    @Nonnull
    public String getFtpUrl() {
        return "ftp://" + (StringUtils.isEmpty(username) ? "" : username + "@") + host + ":" + port;
    }

    @Nonnull
    public LocalCopyManager getLocalCopyManager() {
        return localCopyManager;
    }
}
