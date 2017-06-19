package com.pankratyev.jetbrains.filebrowser.vfs.local.user;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Provides list of "default" user directories like Documents, Pictures, Downloads etc.
 */
public interface UserDirectoriesProvider {
    /**
     * @return absolute paths of user directories. Which directories are included depends on implementation.
     */
    @Nonnull
    Collection<String> getUserDirectories();

    /**
     * @return user home directory; if for some reason it cannot be determined current working directory is returned.
     */
    @Nonnull
    String getHomeDirectory();
}
