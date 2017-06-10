package com.pankratyev.jetbrains.filebrowser.vfs.local.user;

import javax.annotation.Nonnull;

final class DefaultUserDirectoriesProvider extends AbstractUserDirectoriesProvider {
    @Nonnull
    @Override
    protected String[] getUserDirectoriesNames() {
        // return nothing; though there's a possibility to guess some folders without knowing the OS
        return new String[0];
    }
}
