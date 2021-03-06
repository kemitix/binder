package net.kemitix.binder.docx;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.FontSpec;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;
import org.apache.xmlgraphics.image.loader.ImageSize;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@ApplicationScoped
public class DocxImageFacade {

    private final TextImageFactory textImageFactory;
    private final Metadata metadata;

    private final int dpi = 640;
    private final Map<FontSize, ImagePartCache> imagePartCaches =
            new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();
    private final ObjectFactory objectFactory = Context.getWmlObjectFactory();

    @Inject
    public DocxImageFacade(
            TextImageFactory textImageFactory,
            Metadata metadata
    ) {
        this.textImageFactory = textImageFactory;
        this.metadata = metadata;
    }

    public Drawing[] textImages(
            String text,
            FontSize fontSize,
            DocxFacade docx
    ) {
        var imagePartCache = getImagePartCache(fontSize);
        var fontSpec = FontSpec.builder()
                .size(fontSize)
                .kerning(metadata.isKerning())
                .ligatures(metadata.isLigatures())
                .build();
        return words(text)
                .flatMap(word -> textImageFactory.createImages(word, fontSpec).stream())
                .map(image -> imagePart(image, imagePartCache, docx))
                .map(imagePart -> inline(imagePart, fontSize))
                .map(this::drawing)
                .toArray(Drawing[]::new);
    }

    private ImagePartCache getImagePartCache(FontSize fontSize) {
        return imagePartCaches.computeIfAbsent(fontSize,
                (fs) -> ImagePartCache.create());
    }

    private Drawing drawing(Inline inline) {
        try {
            var drawing = objectFactory.createDrawing();
            drawing.getAnchorOrInline()
                    .add(inline);
            return drawing;
        } catch (Exception e) {
            throw new RuntimeException("Error creating drawing", e);
        }
    }

    private Inline inline(
            BinaryPartAbstractImage imagePart,
            FontSize fontSize
    ) {
        try {
            int bodyWidthTwips = getBodyWidthTwips();
            int imageWidthTwips = getImageWidthTwips(imagePart);
            int imageHeightTwips = getImageHeightTwips(imagePart);
            long cx =
                    UnitsOfMeasurement.twipToEMU(
                            Math.min(
                                    bodyWidthTwips,
                                    imageWidthTwips
                            ))
                    ;
            long cy = UnitsOfMeasurement.twipToEMU(imageHeightTwips);
            return imagePart
                    .createImageInline(
                            imagePart.getPartName().getName(),
                            imagePart.getContentType(),
                            idCounter.incrementAndGet(),
                            idCounter.incrementAndGet(),
                            cx,
                            cy,
                            false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BinaryPartAbstractImage imagePart(
            TextImage image,
            Map<String, BinaryPartAbstractImage> imagePartCache,
            DocxFacade docx
    ) {
        return imagePartCache.computeIfAbsent(image.getWord(),
                word -> {
                    try {
                        return BinaryPartAbstractImage
                                .createImagePart(docx.mlPackage(), image.getFile());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Stream<String> words(String text) {
        String[] lines = text.split(System.lineSeparator());
        return Arrays.stream(lines)
                .flatMap(line -> Arrays.stream(line.split("\s+")))
                .filter(word -> word.length() > 0);
    }

    private int getImageWidthTwips(BinaryPartAbstractImage imagePart) {
        return UnitsOfMeasurement
                .inchToTwip(
                        getImageSize(imagePart)
                                .getWidthPx()
                ) / dpi;
    }

    private int getImageHeightTwips(BinaryPartAbstractImage imagePart) {
        return UnitsOfMeasurement
                .inchToTwip(
                        getImageSize(imagePart)
                                .getHeightPx()
                ) / dpi;
    }

    private ImageSize getImageSize(BinaryPartAbstractImage imagePart) {
        //noinspection deprecation
        return imagePart
                .getImageInfo()
                .getSize();
    }

    private int getBodyWidthTwips() {
        float marginSides = metadata.getPaperbackMarginSides();
        float pageWidthInches = metadata.getPaperbackPageWidthInches();
        float bodyWidthInches = pageWidthInches - (2 * marginSides);
        return UnitsOfMeasurement.inchToTwip(bodyWidthInches);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ImagePartCache
            implements Map<String, BinaryPartAbstractImage> {

        @Delegate
        Map<String, BinaryPartAbstractImage> delegate =
                new HashMap<>();

        public static ImagePartCache create() {
            return new ImagePartCache();
        }
    }

}
