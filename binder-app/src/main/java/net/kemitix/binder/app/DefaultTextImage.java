package net.kemitix.binder.app;

import lombok.Getter;
import net.kemitix.binder.spi.FontSpec;
import net.kemitix.binder.spi.TextImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.awt.Image.SCALE_SMOOTH;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class DefaultTextImage
        implements TextImage {

    @Getter
    private final String word;
    private final FontSpec fontSpec;
    private final BufferedImage bufferedImage;
    @Getter
    private final File file;

    public DefaultTextImage(
            String word,
            FontSpec fontSpec,
            BufferedImage bufferedImage
    ) {
        this.word = word;
        this.fontSpec = fontSpec;
        this.bufferedImage = bufferedImage;
        file = new File("text-image-%s-%s.png".formatted(fontSpec.signature(), word));
        writeImageFile();
    }

    @Override
    public byte[] getBytes() {
        return readImageFile();
    }

    private byte[] readImageFile() {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error reading Image for '%s': %s".formatted(word, file.getAbsolutePath()),
                    e);
        }
    }

    private void writeImageFile() {
        try {
            ImageIO.write(bufferedImage, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error writing Image for %s: %s"
                            .formatted(
                                    word, file.getAbsolutePath()),
                    e);
        }
    }

    @Override
    public int getWidth() {
        return bufferedImage.getWidth();
    }

    @Override
    public TextImage withWidth(int maxWidth) {
        int imageWidth = bufferedImage.getWidth();
        float ratio = (float) maxWidth / (float) imageWidth;
        int height = (int) (bufferedImage.getHeight() * ratio);
        BufferedImage resized =
                new BufferedImage(maxWidth, height, TYPE_INT_ARGB);
        Image scaledInstance =
                bufferedImage.getScaledInstance(maxWidth, height, SCALE_SMOOTH);
        Graphics2D graphics = resized.createGraphics();
        graphics.drawImage(scaledInstance, 0, 0, null);
        return new DefaultTextImage(word, fontSpec, resized);
    }
}
