package com.pankratyev.jetbrains.filebrowser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestUtils {
    private TestUtils() {
    }

    public static void deleteFiles(Path... files) {
        if (files == null) {
            return;
        }
        for (Path file : files) {
            if (file == null) {
                continue;
            }
            try {
                Files.delete(file); //TODO handle non-empty directories
            } catch (IOException ignore) { //TODO handle
            }
        }
    }
}
