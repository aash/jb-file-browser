package com.pankratyev.jetbrains.filebrowser.vfs.local.user;

import javax.annotation.Nonnull;

final class WindowsUserDirectoriesProvider extends AbstractUserDirectoriesProvider {
    @Nonnull
    @Override
    protected String[] getUserDirectoriesNames() {
        return new String[] {
                "Documents",
                "Downloads",
                "Pictures",
                "Desktop"
        };
    }
}
