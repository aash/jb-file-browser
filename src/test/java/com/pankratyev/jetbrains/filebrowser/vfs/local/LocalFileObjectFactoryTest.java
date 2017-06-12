package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public final class LocalFileObjectFactoryTest {
    @Test
    public void testCreateByStr() {
        LocalFileObject res = LocalFileObjectFactory.create("/a/b/c/");
        verifyResult(res);
    }

    @Test
    public void testCreateByPath() {
        LocalFileObject res = LocalFileObjectFactory.create(Paths.get("/a/b/c/"));
        verifyResult(res);
    }

    private static void verifyResult(LocalFileObject fileObject) {
        assertEquals("c", fileObject.getName());
        assertEquals("/a/b/c", fileObject.getFullName());

        FileObject parent1 = fileObject.getParent();
        assertNotNull(parent1);
        assertEquals("b", parent1.getName());
        assertEquals("/a/b", parent1.getFullName());

        FileObject parent2 = parent1.getParent();
        assertNotNull(parent2);
        assertEquals("a", parent2.getName());
        assertEquals("/a", parent2.getFullName());
    }
}
