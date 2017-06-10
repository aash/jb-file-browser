package com.pankratyev.jetbrains.filebrowser.vfs.local.user;

import org.apache.commons.lang3.SystemUtils;

public final class UserDirectoriesProviderFactory {
    private UserDirectoriesProviderFactory() {
    }

    public static UserDirectoriesProvider getUserDirectoriesProvider() {
        if (SystemUtils.IS_OS_MAC) {
            return new MacUserDirectoriesProvider();
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return new WindowsUserDirectoriesProvider();
        } else if (SystemUtils.IS_OS_LINUX) {
            return new LinuxUserDirectoriesProvider();
        } else {
            return new DefaultUserDirectoriesProvider();
        }
    }
}
