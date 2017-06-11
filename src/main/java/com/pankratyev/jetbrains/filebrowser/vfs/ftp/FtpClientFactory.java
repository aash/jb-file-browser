package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import org.apache.commons.net.ftp.FTPClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

public final class FtpClientFactory {
    private FtpClientFactory() {
    }

    /**
     * @param host
     * @param port
     * @param username
     * @param password
     * @return initialized {@link FTPClient} with established connection.
     * @throws IOException if connection cannot be established (because of network problems or wrong credentials).
     */
    public static FTPClient createClient(@Nonnull String host, int port,
            @Nullable String username, @Nullable String password) throws IOException {
        FTPClient client = new FTPClient();
        client.connect(host, port);

        if (username != null && password != null) {
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
