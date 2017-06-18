package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.StubFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.TextFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.UnknownFileType;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

public final class SimpleFileTypeProviderTest {
    @Test
    public void testGetType() {
        FileType type = new SimpleFileTypeProvider().getType(new StubFileObject() {
            @Nonnull
            @Override
            public String getName() {
                return "test.txt";
            }
        });
        assertTrue(type.getClass().getName(), type instanceof TextFileType);
    }

    @Test
    public void testGetTypeForNameWithoutExtension() {
        FileType type = new SimpleFileTypeProvider().getType(new StubFileObject() {
            @Nonnull
            @Override
            public String getName() {
                return "test";
            }
        });
        assertTrue(type.getClass().getName(), type instanceof UnknownFileType);
    }

    @Test
    public void testGetTypeForNameEndingWithDot() {
        FileType type = new SimpleFileTypeProvider().getType(new StubFileObject() {
            @Nonnull
            @Override
            public String getName() {
                return "test.";
            }
        });
        assertTrue(type.getClass().getName(), type instanceof UnknownFileType);
    }

    @Test
    public void testGetTextTypeFromContent() {
        FileType type = new SimpleFileTypeProvider().getType(new StubFileObject() {
            @Nonnull
            @Override
            public String getName() {
                return "test";
            }

            @Nonnull
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("this is a text".getBytes(StandardCharsets.UTF_8));
            }
        });
        assertTrue(type.getClass().getName(), type instanceof TextFileType);
    }

    @Test
    public void testGetUnknownTypeFromContent() {
        FileType type = new SimpleFileTypeProvider().getType(new StubFileObject() {
            @Nonnull
            @Override
            public String getName() {
                return "test";
            }

            @Nonnull
            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(new byte[] {0, -1, -2});
            }
        });
        assertTrue(type.getClass().getName(), type instanceof UnknownFileType);
    }
}
