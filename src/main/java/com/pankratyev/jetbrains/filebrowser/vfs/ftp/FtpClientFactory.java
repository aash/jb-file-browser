package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public final class FtpClientFactory {
    private FtpClientFactory() {
    }

    /**
     * @param host host to connect to; shouldn't have "ftp://" prefix.
     * @param port port to use.
     * @param username username, may be null or empty.
     * @param password password, may be null or empty.
     * @return initialized {@link FTPClient} with established connection.
     * @throws IOException if connection cannot be established (because of network problems or wrong credentials).
     */
    public static FTPClient createClient(@Nonnull String host, int port,
            @Nullable String username, @Nullable String password) throws IOException {
        FTPClient client = new FTPClient();
        client.connect(host, port);

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            try {
                boolean loggedIn = client.login(username, password);
                if (!loggedIn) {
                    throw new IOException("Login failed; wrong credentials?");
                }
            } catch (IOException | RuntimeException e) {
                try {
                    client.disconnect();
                } catch (IOException | RuntimeException disconnectEx) {
                    e.addSuppressed(disconnectEx);
                }
                throw e;
            }
        }

        return client;
    }
}
