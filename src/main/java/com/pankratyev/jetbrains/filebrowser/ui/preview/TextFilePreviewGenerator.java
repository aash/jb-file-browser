package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;

public final class TextFilePreviewGenerator implements PreviewGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(PreviewGenerator.class);

    private static final int LINE_COUNT_LIMIT = 100;

    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) {
        try {
            try (InputStream is = fileObject.getInputStream()) {
                if (is != null) {
                    try (LineNumberReader reader = new LineNumberReader(
                            new InputStreamReader(is, StandardCharsets.UTF_8))) {
                        StringBuilder previewContent = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (reader.getLineNumber() > LINE_COUNT_LIMIT) {
                                previewContent.append(System.lineSeparator()).append("...");
                                break;
                            }
                            previewContent.append(line).append(System.lineSeparator());
                        }

                        JTextPane previewPane = new JTextPane();
                        previewPane.setEditable(false);
                        previewPane.setMaximumSize(new Dimension(maxWidth, maxHeight));
                        previewPane.setBorder(BorderFactory.createCompoundBorder(
                                null, BorderFactory.createEmptyBorder(3, 3, 3, 3)));
                        previewPane.setText(previewContent.toString());

                        JScrollPane scrollPane = new JScrollPane(previewPane);
                        scrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
                        return scrollPane;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Cannot generate a preview for a text file: " + fileObject, e);
        }

        return PreviewUtils.getBrokenFilePreview(maxWidth, maxHeight);
    }
}
