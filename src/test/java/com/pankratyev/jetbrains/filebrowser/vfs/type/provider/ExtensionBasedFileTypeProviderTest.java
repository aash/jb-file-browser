package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.ui.filetype.provider.ExtensionBasedFileTypeProvider;
import com.pankratyev.jetbrains.filebrowser.vfs.StubFileObject;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.FileType;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.TextFileType;
import com.pankratyev.jetbrains.filebrowser.ui.filetype.UnknownFileType;
import org.junit.Test;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertTrue;

public final class ExtensionBasedFileTypeProviderTest {
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
