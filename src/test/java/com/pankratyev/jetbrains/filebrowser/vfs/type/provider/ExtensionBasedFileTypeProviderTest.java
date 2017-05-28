package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.StubFileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.TextFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.UnknownFileType;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertTrue;

public class ExtensionBasedFileTypeProviderTest {
    @Test
    public void testGetType() {
        FileType type = new ExtensionBasedFileTypeProvider().getType(new StubFileObject() {
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
        FileType type = new ExtensionBasedFileTypeProvider().getType(new StubFileObject() {
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
        FileType type = new ExtensionBasedFileTypeProvider().getType(new StubFileObject() {
            @Nonnull
            @Override
            public String getName() {
                return "test.";
            }
        });
        assertTrue(type.getClass().getName(), type instanceof UnknownFileType);
    }
}