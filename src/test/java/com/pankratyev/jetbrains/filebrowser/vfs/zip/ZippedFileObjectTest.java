package com.pankratyev.jetbrains.filebrowser.vfs.zip;

import com.pankratyev.jetbrains.filebrowser.TestUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.VfsUtils;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.local.LocalFileObjectFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class ZippedFileObjectTest {
    @Test
    public void testZipArchiveFolderHierarchy() throws IOException {
        Path topDir = null;
        try {
            topDir = Files.createTempDirectory("LocalFileObjectTest.testZipArchiveFolderHierarchy");
            Path subDir = Files.createTempDirectory(topDir, "LocalFileObjectTest.testZipArchiveFolderHierarchy");
            Path subSubDir = Files.createTempDirectory(subDir, "LocalFileObjectTest.testZipArchiveFolderHierarchy");
            Path subSubSubDir = Files.createTempDirectory(
                    subSubDir, "LocalFileObjectTest.testZipArchiveFolderHierarchy");

            Path archivePath = Files.createTempFile("LocalFileObjectTest.testZipArchiveFolderHierarchy", ".zip");
            TestUtils.zipDirectory(topDir, archivePath);

            LocalFileObject archive = LocalFileObjectFactory.create(archivePath);
            assertTrue(ZipUtils.isZipArchive(archive));

            Collection<FileObject> children1 = archive.getChildren();
            assertNotNull(children1);
            assertEquals(children1.toString(), 1, children1.size());
            FileObject child1 = children1.iterator().next();
            assertEquals(subDir.getFileName().toString(),
                    VfsUtils.normalizePath(child1.getName(), ZipUtils.ZIP_PATH_SEPARATOR));
            assertEquals(archive, child1.getParent());

            Collection<FileObject> children2 = child1.getChildren();
            assertNotNull(children2);
            assertEquals(children2.toString(), 1, children2.size());
            FileObject child2 = children2.iterator().next();
            assertEquals(subSubDir.getFileName().toString(),
                    VfsUtils.normalizePath(child2.getName(), ZipUtils.ZIP_PATH_SEPARATOR));
            assertEquals(child1, child2.getParent());

            Collection<FileObject> children3 = child2.getChildren();
            assertNotNull(children3);
            assertEquals(children3.toString(), 1, children3.size());
            FileObject child3 = children3.iterator().next();
            assertEquals(subSubSubDir.getFileName().toString(),
                    VfsUtils.normalizePath(child3.getName(), ZipUtils.ZIP_PATH_SEPARATOR));
            assertEquals(child2, child3.getParent());
        } finally {
            TestUtils.deleteFiles(topDir);
        }
    }

    @Test
    public void testNestedArchive() throws Exception {
        Path topDir = null;
        try {
            topDir = Files.createTempDirectory("ZippedFileObjectTest.testNestedArchive");
            Path topArchivePath = Files.createTempFile(topDir, "topArchive", ".zip");
            Path subArchivePath = Files.createTempFile(topDir, "subArchive", ".zip");
            Path subSubArchivePath = Files.createTempFile(topDir, "subSubArchive", ".zip");
            Path file = Files.createTempFile(topDir, "file", ".txt");
            Files.write(file, "test".getBytes(StandardCharsets.UTF_8));

            TestUtils.zipSingleFile(file, subSubArchivePath);
            TestUtils.zipSingleFile(subSubArchivePath, subArchivePath);
            TestUtils.zipSingleFile(subArchivePath, topArchivePath);

            LocalFileObject archive = LocalFileObjectFactory.create(topArchivePath);
            assertTrue(ZipUtils.isZipArchive(archive));

            Collection<FileObject> children1 = archive.getChildren();
            assertNotNull(children1);
            assertEquals(children1.toString(), 1, children1.size());
            FileObject child1 = children1.iterator().next();
            assertEquals(subArchivePath.getFileName().toString(),
                    VfsUtils.normalizePath(child1.getName(), ZipUtils.ZIP_PATH_SEPARATOR));
            assertEquals(archive, child1.getParent());

            Collection<FileObject> children2 = child1.getChildren();
            assertNotNull(children2);
            assertEquals(children2.toString(), 1, children2.size());
            FileObject child2 = children2.iterator().next();
            assertEquals(subSubArchivePath.getFileName().toString(),
                    VfsUtils.normalizePath(child2.getName(), ZipUtils.ZIP_PATH_SEPARATOR));
            assertEquals(child1, child2.getParent());

            Collection<FileObject> children3 = child2.getChildren();
            assertNotNull(children3);
            assertEquals(children3.toString(), 1, children3.size());
            FileObject child3 = children3.iterator().next();
            assertEquals(file.getFileName().toString(),
                    VfsUtils.normalizePath(child3.getName(), ZipUtils.ZIP_PATH_SEPARATOR));
            assertEquals(child2, child3.getParent());

            try (InputStream is = child3.getInputStream()) {
                assertNotNull(is);
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);
                assertEquals("test", content);
            }
        } finally {
            TestUtils.deleteFiles(topDir);
        }
    }
}
