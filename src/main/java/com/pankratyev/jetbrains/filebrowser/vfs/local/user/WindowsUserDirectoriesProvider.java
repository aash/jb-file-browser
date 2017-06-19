package com.pankratyev.jetbrains.filebrowser.vfs.local.user;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @Nonnull
    @Override
    public Collection<String> getUserDirectories() {
        // list all disks
        File[] roots = File.listRoots();
        List<String> dirs = new ArrayList<>();
        for (File root : roots) {
            dirs.add(root.getAbsolutePath());
        }
        dirs.addAll(super.getUserDirectories());
        return dirs;
    }
}
