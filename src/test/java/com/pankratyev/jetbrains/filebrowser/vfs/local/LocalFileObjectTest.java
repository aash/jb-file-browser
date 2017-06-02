package com.pankratyev.jetbrains.filebrowser.vfs.local;

import com.pankratyev.jetbrains.filebrowser.TestUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LocalFileObjectTest {
    @Test
    public void testLocalFile() throws IOException {
        Path file = null;
        try {
            file = Files.createTempFile("LocalFileObjectTest.testLocalFile", null);
            Files.write(file, "test".getBytes(StandardCharsets.UTF_8));

            LocalFileObject subj = new LocalFileObject(null, file);
            assertFalse(subj.isDirectory());
            assertNull(subj.getChildren());
            assertNull(subj.getParent());

            assertEquals(file.toAbsolutePath().toString(), subj.getFullName());
            assertEquals(file.getFileName().toString(), subj.getName());

            try (InputStream is = subj.getInputStream()) {
                assertNotNull(is);
                assertEquals("test", IOUtils.toString(is, StandardCharsets.UTF_8));
            }
        } finally {
            TestUtils.deleteFiles(file);
        }
    }

    @Test
    public void testLocalEmptyDirectory() throws IOException {
        Path dir = null;
        try {
            dir = Files.createTempDirectory("LocalFileObjectTest.testLocalEmptyDirectory");

            LocalFileObject subj = new LocalFileObject(null, dir);
            assertTrue(subj.isDirectory());
            assertNotNull(subj.getChildren());
            assertTrue(subj.getChildren().toString(), subj.getChildren().isEmpty());
            assertNull(subj.getParent());

            assertEquals(dir.toAbsolutePath().toString(), subj.getFullName());
            assertEquals(dir.getFileName().toString(), subj.getName());

            assertNull(subj.getInputStream());
        } finally {
            TestUtils.deleteFiles(dir);
        }
    }

    @Test
    public void testLocalHierarchy() throws IOException {
        Path dirPath = null;
        Path subDirPath = null;
        Path file1Path = null;
        Path file2Path = null;
        Path subFile1Path = null;
        try {
            dirPath = Files.createTempDirectory("LocalFileObjectTest.testLocalHierarchy");
            subDirPath = Files.createTempDirectory(dirPath, "LocalFileObjectTest.testLocalHierarchy");
            file1Path = Files.createTempFile(dirPath, "LocalFileObjectTest.testLocalHierarchy", null);
            file2Path = Files.createTempFile(dirPath, "LocalFileObjectTest.testLocalHierarchy", null);
            subFile1Path = Files.createTempFile(subDirPath, "LocalFileObjectTest.testLocalHierarchy", null);

            Files.write(file1Path, "file1 content".getBytes(StandardCharsets.UTF_8));
            Files.write(file2Path, "file2 content".getBytes(StandardCharsets.UTF_8));
            // leave subFile1Path empty

            LocalFileObject dir = new LocalFileObject(null, dirPath);
            LocalFileObject subDir = new LocalFileObject(dir, subDirPath);
            LocalFileObject file1 = new LocalFileObject(dir, file1Path);
            LocalFileObject file2 = new LocalFileObject(dir, file2Path);
            LocalFileObject subFile1 = new LocalFileObject(subDir, subFile1Path);

            assertTrue(dir.isDirectory());
            assertTrue(subDir.isDirectory());
            assertFalse(file1.isDirectory());
            assertFalse(file2.isDirectory());
            assertFalse(subFile1.isDirectory());

            assertNull(file1.getChildren());
            assertNull(file2.getChildren());
            assertNull(subFile1.getChildren());
            assertNotNull(dir.getChildren());
            assertNotNull(subDir.getChildren());

            assertEquals(3, dir.getChildren().size());
            Set<FileObject> expectedDirChildren = new HashSet<>();
            expectedDirChildren.add(file1);
            expectedDirChildren.add(file2);
            expectedDirChildren.add(subDir);
            assertEquals(expectedDirChildren, new HashSet<>(dir.getChildren()));
            assertEquals(1, subDir.getChildren().size());
            assertEquals(Collections.singleton(subFile1), new HashSet<>(subDir.getChildren()));

            assertEquals(dir, file1.getParent());
            assertEquals(dir, file2.getParent());
            assertEquals(dir, subDir.getParent());
            assertEquals(subDir, subFile1.getParent());
            assertNull(dir.getParent());

            assertEquals(dirPath.toAbsolutePath().toString(), dir.getFullName());
            assertEquals(dirPath.getFileName().toString(), dir.getName());
            assertEquals(subDirPath.toAbsolutePath().toString(), subDir.getFullName());
            assertEquals(subDirPath.getFileName().toString(), subDir.getName());
            assertEquals(file1Path.toAbsolutePath().toString(), file1.getFullName());
            assertEquals(file1Path.getFileName().toString(), file1.getName());
            assertEquals(file2Path.toAbsolutePath().toString(), file2.getFullName());
            assertEquals(file2Path.getFileName().toString(), file2.getName());
            assertEquals(subFile1Path.toAbsolutePath().toString(), subFile1.getFullName());
            assertEquals(subFile1Path.getFileName().toString(), subFile1.getName());

            assertNull(dir.getInputStream());
            assertNull(subDir.getInputStream());
            try (InputStream is1 = file1.getInputStream(); InputStream is2 = file2.getInputStream();
                    InputStream is3 = subFile1.getInputStream()) {
                assertNotNull(is1);
                assertNotNull(is2);
                assertNotNull(is3);
                assertEquals("file1 content", IOUtils.toString(is1, StandardCharsets.UTF_8));
                assertEquals("file2 content", IOUtils.toString(is2, StandardCharsets.UTF_8));
                byte[] is3Bytes = IOUtils.toByteArray(is3);
                assertEquals(Arrays.toString(is3Bytes), 0, is3Bytes.length);
            }
        } finally {
            TestUtils.deleteFiles(dirPath, subDirPath, file1Path, file2Path, subFile1Path);
        }
    }

}