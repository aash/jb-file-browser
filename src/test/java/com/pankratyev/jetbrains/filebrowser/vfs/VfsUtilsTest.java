package com.pankratyev.jetbrains.filebrowser.vfs;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public final class VfsUtilsTest {
    @Test
    public void testNormalizePath() {
        assertEquals("test1\\test2", VfsUtils.normalizePath("\\test1\\test2\\", "\\"));
        assertEquals("test1\\test2", VfsUtils.normalizePath("test1\\test2\\", "\\"));
        assertEquals("test1\\test2", VfsUtils.normalizePath("\\test1\\test2", "\\"));
    }

    @Test
    public void testGetNameFromAbsolutePath() {
        assertEquals("test2", VfsUtils.getNameFromAbsolutePath(
                File.separator + "test1" + File.separator + "test2"));
        assertEquals("test2", VfsUtils.getNameFromAbsolutePath(
                File.separator + "test1" + File.separator + "test2" + File.separator));
    }
}
