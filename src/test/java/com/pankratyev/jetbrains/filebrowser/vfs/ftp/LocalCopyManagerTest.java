package com.pankratyev.jetbrains.filebrowser.vfs.ftp;

import com.pankratyev.jetbrains.filebrowser.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocalCopyManager.class)
public class LocalCopyManagerTest {
    @Test
    public void testGetLocalCopyOutputStream() throws IOException {
        LocalCopyManager subj = new LocalCopyManager("testhost1");
        String testPath = separator + "absolute" + separator + "path" + separator + "testGetLocalCopyOutputStream";
        FtpFileObject testFileObject = new FtpFileObject(new FtpClient("", 0, null, null), testPath, null, false, subj);

        try (OutputStream os = subj.getLocalCopyOutputStream(testFileObject)) {
            assertNotNull(os);
            try (OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                osw.write("testGetLocalCopyOutputStream");
            }
        }

        Path localCopyPath = Paths.get(LocalCopyManager.BASE_DIRECTORY + File.separator + "testhost1" + testPath);
        try {
            assertTrue(localCopyPath.toString(), Files.exists(localCopyPath));
            String content = new String(Files.readAllBytes(localCopyPath), StandardCharsets.UTF_8);
            assertNotNull("testGetLocalCopyOutputStream", content);
        } finally {
            TestUtils.deleteFiles(localCopyPath);
        }
    }

    @Test
    public void testGetLocalCopy() throws IOException {
        LocalCopyManager subj = new LocalCopyManager("testhost2");
        String testPath = separator + "absolute" + separator + "path" + separator + "testGetLocalCopy";
        Path localCopyPath = Paths.get(LocalCopyManager.BASE_DIRECTORY + File.separator + "testhost2" + testPath);

        try {
            Files.createDirectories(localCopyPath.getParent());
            Files.write(localCopyPath, "testGetLocalCopy".getBytes(StandardCharsets.UTF_8));

            FtpFileObject testFileObject = new FtpFileObject(
                    new FtpClient("", 0, null, null), testPath, null, false, subj);
            Path localCopy = subj.getLocalCopy(testFileObject);
            assertNotNull(localCopy);
            String content = new String(Files.readAllBytes(localCopy), StandardCharsets.UTF_8);
            assertEquals("testGetLocalCopy", content);
        } finally {
            TestUtils.deleteFiles(localCopyPath);
        }
    }


    @Test
    public void testExpire() throws Exception {
        LocalCopyManager subj = new LocalCopyManager("testhost3");
        String testPath = separator + "absolute" + separator + "path" + separator + "testExpire";
        Path localCopyPath = Paths.get(LocalCopyManager.BASE_DIRECTORY + File.separator + "testhost3" + testPath);

        try {
            Files.createDirectories(localCopyPath.getParent());
            Files.createFile(localCopyPath);

            FtpFileObject testFileObject = new FtpFileObject(
                    new FtpClient("", 0, null, null), testPath, null, false, subj);
            assertNotNull(subj.getLocalCopy(testFileObject));

            // set very short expire interval
            LocalCopyManager spy = spy(subj);
            int expireInterval = 1;
            doReturn(expireInterval).when(spy, "getLocalCopyExpireTimeInterval");

            Thread.sleep(expireInterval);
            // should be null (expired) on second request (on spy this time)
            assertNull(spy.getLocalCopy(testFileObject));
        } finally {
            TestUtils.deleteFiles(localCopyPath);
        }
    }
}
