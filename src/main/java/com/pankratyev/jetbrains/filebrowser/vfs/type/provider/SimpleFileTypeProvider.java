package com.pankratyev.jetbrains.filebrowser.vfs.type.provider;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import com.pankratyev.jetbrains.filebrowser.vfs.type.ArchiveFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.DirectoryFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.FileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.ImageFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.TextFileType;
import com.pankratyev.jetbrains.filebrowser.vfs.type.UnknownFileType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@link FileTypeProvider}.
 * Determines file type by extension. If extension is not known tries to determine if the file is a text one by reading
 * some of its content from the beginning.
 */
public final class SimpleFileTypeProvider implements FileTypeProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFileTypeProvider.class);

    private static final int PROBE_BYTE_COUNT = 100;

    private static final ImageFileType IMAGE_TYPE = new ImageFileType();
    private static final TextFileType TEXT_TYPE = new TextFileType();
    private static final ArchiveFileType ARCHIVE_TYPE = new ArchiveFileType();
    private static final UnknownFileType UNKNOWN_TYPE = new UnknownFileType();

    private static final Map<String, FileType> FILE_TYPES = new HashMap<>();
    static {
        FILE_TYPES.put("jpeg", IMAGE_TYPE);
        FILE_TYPES.put("jpg", IMAGE_TYPE);
        FILE_TYPES.put("png", IMAGE_TYPE);
        FILE_TYPES.put("bmp", IMAGE_TYPE);
        FILE_TYPES.put("gif", IMAGE_TYPE);
        FILE_TYPES.put("txt", TEXT_TYPE);
        FILE_TYPES.put("log", TEXT_TYPE);
        FILE_TYPES.put("htm", TEXT_TYPE);
        FILE_TYPES.put("html", TEXT_TYPE);
        FILE_TYPES.put("xml", TEXT_TYPE);
        FILE_TYPES.put("json", TEXT_TYPE);
        FILE_TYPES.put("sh", TEXT_TYPE);
        FILE_TYPES.put("bat", TEXT_TYPE);
        FILE_TYPES.put("ini", TEXT_TYPE);
        FILE_TYPES.put("iml", TEXT_TYPE);
        FILE_TYPES.put("js", TEXT_TYPE);
        FILE_TYPES.put("md", TEXT_TYPE);
        FILE_TYPES.put("zip", ARCHIVE_TYPE);
    }

    @Nonnull
    @Override
    public FileType getType(FileObject file) {
        if (file.isDirectory()) {
            return new DirectoryFileType();
        }
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if (extension.isEmpty()) {
            return determineByContent(file);
        }
        FileType type = FILE_TYPES.get(extension);

        return type != null ? type : determineByContent(file);
    }

    private static FileType determineByContent(FileObject file) {
        try (InputStream is = file.getInputStream()) {
            if (is != null) {
                byte[] firstBytes = new byte[PROBE_BYTE_COUNT];
                int readCount = IOUtils.read(is, firstBytes, 0, PROBE_BYTE_COUNT);
                if (readCount == 0) {
                    return UNKNOWN_TYPE;
                }

                if (readCount < PROBE_BYTE_COUNT) {
                    byte[] newFirstBytes = new byte[readCount];
                    System.arraycopy(firstBytes, 0, newFirstBytes, 0, readCount);
                    firstBytes = newFirstBytes;
                }

                try {
                    for (byte b : firstBytes) {
                        if (b == 0) {
                            // it is unlikely that this is a text file;
                            // and it IS likely that 0 byte will cause false positive text type detection
                            return UNKNOWN_TYPE;
                        }
                    }

                    CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
                    decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
                    decoder.decode(ByteBuffer.wrap(firstBytes));
                    return TEXT_TYPE;
                } catch (CharacterCodingException ignore) {
                }
            }
        } catch (FileSystemException ignore) {
            // it happens on Windows for some specific files like ntuser.dat.LOG
        } catch (IOException e) {
            LOGGER.warn("An error occurred while probing the file", e);
        }

        return UNKNOWN_TYPE;
    }
}
