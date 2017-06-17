package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Auto-reconnecting FTP client with {@link FileObject}-based API.
 */
public final class FtpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpClient.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private FTPClient client = null;

    public FtpClient(@Nonnull String host, int port, @Nullable String username, @Nullable String password) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.username = username;
        this.password = password;
    }


    /**
     * @return current directory. It can be used right after FTP connection is established to display initial directory.
     * @throws IOException
     */
    public FileObject getCurrentDirectory() throws IOException {
        ensureClientReady();

        String currentDirAbsolutePath = client.printWorkingDirectory();
        return new FtpFileObject(this, currentDirAbsolutePath, null, true, new LocalCopyManager(host));
    }

    /**
     * @param directory directory to get children for.
     * @return children of passed directory.
     * @throws IOException
     */
    //TODO this method should also support zip
    List<FileObject> list(FtpFileObject directory) throws IOException {
        ensureClientReady();

        client.changeWorkingDirectory(directory.getFullName());
        FTPFile[] files = client.listFiles();
        String currentDirectoryPath = directory.getFullName();
        List<FileObject> children = new ArrayList<>();
        for (FTPFile file : files) {
            String fileAbsolutePath = currentDirectoryPath + File.separator + file.getName();
            children.add(new FtpFileObject(
                    this, fileAbsolutePath, directory, file.isDirectory(), directory.getLocalCopyManager()));
        }

        return children;
    }

    /**
     * @param file file to get {@link InputStream} for.
     * @return buffered {@link InputStream} for passed {@link FtpFileObject}.
     * @throws IOException
     */
    InputStream getFileStream(FtpFileObject file) throws IOException {
        ensureClientReady();
        return new BufferedInputStream(client.retrieveFileStream(file.getFullName()));
    }

    //TODO pass child directory to this method?
    FileObject getParentDirectory(FtpFileObject fileObject) throws IOException {
        ensureClientReady();

        //TODO review, maybe there's a better way to obtain a parent directory without switching directory two times

        client.changeWorkingDirectory(fileObject.getFullName());
        boolean changedToParent = client.changeToParentDirectory();
        if (!changedToParent) {
            return null;
        }

        FileObject parent = getCurrentDirectory();
        boolean changedBack = client.changeWorkingDirectory(fileObject.getFullName());
        if (!changedBack) {
            disconnect();
            throw new IOException("Directory changed to parent, cannot change back");
        }

        return parent;
    }


    public void ensureClientReady() throws IOException {
        if (client != null) {
            try {
                boolean answer = client.sendNoOp();
                if (!answer) {
                    disconnect();
                }
            } catch (IOException e) {
                LOGGER.debug(null, e);
                disconnect();
            }
        }

        if (client == null) {
            client = new FTPClient();
            client.connect(host, port);

            if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                try {
                    boolean loggedIn = client.login(username, password);
                    if (!loggedIn) {
                        throw new IOException("Login failed; wrong credentials?");
                    }
                } catch (IOException | RuntimeException e) {
                    disconnect();
                    throw e;
                }
            }
        }
    }

    /**
     * Disconnects from FTP server. After that {@link FtpClient} still can be used,
     * connection will be established again if necessary.
     */
    public void disconnect() {
        try {
            client.disconnect();
            LOGGER.debug("Disconnected from {}", host);
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("An error occurred while trying to disconnect from " + host, e);
        }
        client = null;
    }
}
