package com.pankratyev.jetbrains.filebrowser.vfs.local;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class LocalFileObjectFactory {
    @Nonnull
    public static LocalFileObject create(String path) {
        Objects.requireNonNull(path);
        return create(Paths.get(path));
    }

    @Nonnull
    public static LocalFileObject create(Path path) {
        Path absolutePath = path.toAbsolutePath();

        // /a/b/c  ->  /a, /a/b, /a/b/c
        List<Path> paths = new ArrayList<>(absolutePath.getNameCount());
        paths.add(absolutePath);
        Path parent = absolutePath;
        while ((parent = parent.getParent()) != null) {
            paths.add(parent);
        }
        Collections.reverse(paths);

        LocalFileObject res = null;
        for (Path p : paths) {
            res = new LocalFileObject(res, p);
        }

        //noinspection ConstantConditions - it's never actually null
        return res;
    }
}
