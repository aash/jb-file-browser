package com.pankratyev.jetbrains.filebrowser.ui.preview;

import com.pankratyev.jetbrains.filebrowser.vfs.FileObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public final class TextFilePreviewGenerator implements PreviewGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextFilePreviewGenerator.class);

    @Nonnull
    @Override
    public JComponent generatePreview(FileObject fileObject, int maxWidth, int maxHeight) throws IOException {
        JTextPane previewPane = new JTextPane();
        previewPane.setEditable(false);
        previewPane.setMaximumSize(new Dimension(maxWidth, maxHeight));
        previewPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

        FontMetrics fontMetrics = previewPane.getFontMetrics(previewPane.getFont());
        int lineHeight = fontMetrics.getHeight();
        int maxLineCount = maxHeight / lineHeight;

        try (InputStream is = fileObject.getInputStream()) {
            if (is == null) {
                LOGGER.error("Unexpected null input stream for file object: " + fileObject);
                return previewPane;
            }

            try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(is))) {
                StringBuilder previewContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && reader.getLineNumber() <= maxLineCount) {
                    previewContent.append(line).append(System.lineSeparator());
                }

                previewPane.setText(previewContent.toString());
                return previewPane;
            }
        }
    }
}
