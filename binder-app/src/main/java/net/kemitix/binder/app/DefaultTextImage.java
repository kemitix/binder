package net.kemitix.binder.app;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.TextImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Log
public class DefaultTextImage
        implements TextImage {

    private final String word;
    private final int fontSize;
    private final BufferedImage bufferedImage;
    private final File file;

    public DefaultTextImage(
            String word,
            int fontSize,
            BufferedImage bufferedImage
    ) {
        this.word = word;
        this.fontSize = fontSize;
        this.bufferedImage = bufferedImage;
        file = new File("text-image-%d-%s.png".formatted(fontSize, word));
    }

    @Override
    public byte[] getBytes() {
        writeImageFile();
        return new byte[0];//TODO
    }

    private boolean writeImageFile() {
        log.info("Writing %s @ %d (%dx%d): %s".formatted(word, fontSize,
                bufferedImage.getWidth(), bufferedImage.getHeight(),
                file.getAbsolutePath()));
        try {
            return ImageIO.write(bufferedImage, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error writing Image for %s @ %d".formatted(word, fontSize),
                    e);
        }
    }

    @Override
    public int getWidth() {
        return 0;//TODO
    }

    @Override
    public TextImage withWidth(int maxWidth) {
        return this;//TODO
    }
}
