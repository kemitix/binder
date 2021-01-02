package net.kemitix.binder.app;

import net.kemitix.binder.spi.CoverFont;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;
import net.kemitix.fontface.FontCache;
import net.kemitix.fontface.FontFace;
import org.beryx.awt.color.ColorFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class DefaultTextImageFactory
        implements TextImageFactory {

    public static final Predicate<String> IGNORE_BLANKS = word -> word.length() > 0;
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
            FontSize fontSize
    ) {
        Stream<String> words = splitIntoLines(text)
                .flatMap(this::splitIntoWords)
                .filter(IGNORE_BLANKS);
        return words
                .map(word -> createImage(word, fontSize))
                .collect(Collectors.toList());
    }

    private Stream<String> splitIntoLines(String text) {
        return Arrays.stream(text.split(System.lineSeparator()));
    }

    private Stream<String> splitIntoWords(String line) {
        return Arrays.stream(line.split("\s+"));
    }

    private DefaultTextImage createImage(String word, FontSize fontSize) {
        Font titleFont = getFont(fontSize);
        int spaceWidth = fontSize.getValue();
        int padWidth = spaceWidth / 2;
        Rectangle2D stringBounds = getStringBounds(word, titleFont);
        int width = (int) stringBounds.getWidth() + padWidth;
        int height = (int) (stringBounds.getHeight() * 1.2);

        BufferedImage bufferedImage =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(ColorFactory.web("black", 1.0));
        graphics.setFont(titleFont);
        graphics.drawString(
                word,
                padWidth / 2,
                (int) (stringBounds.getHeight() * 0.8));
        return new DefaultTextImage(word, fontSize, bufferedImage);
    }

    private Font getFont(FontSize fontSize) {
        FontFace fontFace = FontFace.of(fontUri, fontSize.getValue(), "black");
        final Map<TextAttribute, Object> map = new HashMap<>();
        map.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
        map.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        return fontCache.loadFont(fontFace)
                .deriveFont(map);
    }

    private Rectangle2D getStringBounds(String word, Font titleFont) {
        return titleFont.getStringBounds(word,
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
                        .createGraphics()
                        .getFontRenderContext());
    }

}
