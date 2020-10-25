package net.kemitix.binder.docx;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;
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
    private final DocxFacade docx;

    private final Map<FontSize, ImagePartCache> imagePartCaches =
            new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();
    private final ObjectFactory objectFactory = Context.getWmlObjectFactory();

    @Inject
    public DocxImageFacade(
            TextImageFactory textImageFactory,
            Metadata metadata,
            DocxFacade docx
    ) {
        this.textImageFactory = textImageFactory;
        this.metadata = metadata;
        this.docx = docx;
    }

    public Drawing[] textImages(String text, FontSize fontSize) {
        var imagePartCache = getImagePartCache(fontSize);
        return words(text)
                .flatMap(word -> textImageFactory.createImages(word, fontSize).stream())
                .map(image -> imagePart(image, imagePartCache))
                .map(this::inline)
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

    private Inline inline(BinaryPartAbstractImage imagePart) {
        try {
            long cx = Math.min(
                    getBodyWidthTwips(),
                    getImageWidthTwips(imagePart));
            //long cy = 0;// fontSize * 1000
            return imagePart
                    .createImageInline(
                            imagePart.getPartName().getName(),
                            imagePart.getContentType(),
                            idCounter.incrementAndGet(),
                            idCounter.incrementAndGet(),
                            cx,
              //              cy,
                            false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BinaryPartAbstractImage imagePart(
            TextImage image,
            Map<String, BinaryPartAbstractImage> imagePartCache
    ) {
        return imagePartCache.computeIfAbsent(image.getWord(),
                word -> {
                    try {
                        return BinaryPartAbstractImage
                                .createImagePart(docx.getMlPackage(), image.getFile());
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
        //noinspection deprecation
        return UnitsOfMeasurement
                .pxToTwip(
                        imagePart
                                .getImageInfo()
                                .getSize()
                                .getWidthPx());
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
