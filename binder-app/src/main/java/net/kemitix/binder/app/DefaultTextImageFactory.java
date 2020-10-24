package net.kemitix.binder.app;

import net.kemitix.binder.spi.CoverFont;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;
import net.kemitix.fontface.FontCache;
import net.kemitix.fontface.FontFace;
import org.apache.pdfbox.rendering.ImageType;
import org.beryx.awt.color.ColorFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class DefaultTextImageFactory
        implements TextImageFactory {

    private final FontCache fontCache;
    private final URI fontUri;

    @Inject
    public DefaultTextImageFactory(
            FontCache fontCache,
            @CoverFont URI fontUri
    ) {
        this.fontCache = fontCache;
        this.fontUri = fontUri;
    }

    @Override
    public List<TextImage> createImages(
            String text,
            FontSize fontSize,
            int pageWidth
    ) {
        String[] lines = text.split(System.lineSeparator());
        Stream<String> words = Arrays.stream(lines)
                .flatMap(line -> Arrays.stream(line.split("\s+")))
                .filter(word -> word.length() > 0);
        return words
                .map(word -> createImage(word, fontSize, pageWidth))
                .collect(Collectors.toList());
    }

    private TextImage createImage(
            String word,
            FontSize fontSize,
            int maxWidth
    ) {
        TextImage image = createImage(word, fontSize);
        if (maxWidth < image.getWidth()) {
            return image.withWidth(maxWidth);
        }
        return image;
    }

    private DefaultTextImage createImage(String word, FontSize fontSize) {
        Font titleFont = getFont(fontSize);
        Rectangle2D stringBounds = getStringBounds(word, titleFont);
        BufferedImage bufferedImage =
                new BufferedImage(
                        (int) stringBounds.getWidth(),
                        (int) stringBounds.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(ColorFactory.web("black", 1.0));
        graphics.setFont(titleFont);
        graphics.drawString(word, 0, (int) (stringBounds.getHeight() * 0.75));
        return new DefaultTextImage(word, fontSize, bufferedImage);
    }

    private Font getFont(FontSize fontSize) {
        FontFace fontFace = FontFace.of(fontUri, fontSize.getValue(), "black");
        return fontCache.loadFont(fontFace);
    }

    private Rectangle2D getStringBounds(String word, Font titleFont) {
        return titleFont.getStringBounds(word,
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                        .createGraphics()
                        .getFontRenderContext());
    }

}
