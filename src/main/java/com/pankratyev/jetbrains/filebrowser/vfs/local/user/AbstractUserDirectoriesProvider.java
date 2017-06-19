package com.pankratyev.jetbrains.filebrowser.vfs.local.user;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class AbstractUserDirectoriesProvider implements UserDirectoriesProvider {
    @Nonnull
    @Override
    public Collection<String> getUserDirectories() {
        List<String> userDirectories = new ArrayList<>();
        String userDirectory = getUserHomeDirectory();
        if (checkDirectoryExists(userDirectory)) {
            userDirectories.add(userDirectory);
            userDirectory += File.separator;
            for (String directoryName : getUserDirectoriesNames()) {
                String directoryAbsolutePath = userDirectory + directoryName;
                if (checkDirectoryExists(directoryAbsolutePath)) {
                    userDirectories.add(directoryAbsolutePath);
                }
            }
        }
        return userDirectories;
    }

    @Nonnull
    @Override
    public String getHomeDirectory() {
        String dir = getUserHomeDirectory();
        if (StringUtils.isEmpty(dir)) {
            dir = System.getProperty("user.dir");
        }
        if (StringUtils.isEmpty(dir)) {
            throw new RuntimeException("Cannot determine user home directory and current working directory");
        }
        return dir;
    }

    private String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

    private boolean checkDirectoryExists(String absolutePath) {
        return Files.isDirectory(Paths.get(absolutePath));
    }

    @Nonnull
    protected abstract String[] getUserDirectoriesNames();
}
