package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.junit.Test;

import java.nio.file.Paths;

import static java.io.File.separator;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class LocalFileObjectFactoryTest {
    @Test
    public void testCreateByStr() {
        LocalFileObject res = LocalFileObjectFactory.create(
                separator + "a" + separator + "b" + separator + "c" + separator);
        verifyResult(res);
    }

    @Test
    public void testCreateByPath() {
        LocalFileObject res = LocalFileObjectFactory.create(Paths.get(
                separator + "a" + separator + "b" + separator + "c" + separator));
        verifyResult(res);
    }

    private static void verifyResult(LocalFileObject fileObject) {
        assertEquals("c", fileObject.getName());
        assertTrue(fileObject.getFullName(),
                fileObject.getFullName().endsWith(separator + "a" + separator + "b" + separator + "c"));

        FileObject parent1 = fileObject.getParent();
        assertNotNull(parent1);
        assertEquals("b", parent1.getName());
        assertTrue(parent1.getFullName(), parent1.getFullName().endsWith(separator + "a" + separator + "b"));

        FileObject parent2 = parent1.getParent();
        assertNotNull(parent2);
        assertEquals("a", parent2.getName());
        assertTrue(parent2.getFullName(), parent2.getFullName().endsWith(separator + "a"));
    }
}
