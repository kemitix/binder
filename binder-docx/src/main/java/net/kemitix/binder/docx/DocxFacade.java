package net.kemitix.binder.docx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.STTabJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tabs;
import org.docx4j.wml.Text;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class DocxFacade {

    private final TextImageFactory textImageFactory;
    private final Metadata metadata;

    @Getter
    private final WordprocessingMLPackage mlPackage;

    private final ObjectFactory objectFactory = Context.getWmlObjectFactory();
    private final Map<FontSize, ImagePartCache> imagePartCaches = new HashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();

    @Inject
    public DocxFacade(
            TextImageFactory textImageFactory,
            Metadata metadata
    ) throws InvalidFormatException {
        this.textImageFactory = textImageFactory;
        this.metadata = metadata;

        mlPackage = WordprocessingMLPackage.createPackage();
    }

    public P breakToOddPage() {
        return p(ppr(sectPr(sectPrType("oddPage"))));
    }

    public P textImage(String text, FontSize fontSize) {
        var imagePartCache = getImagePartCache(fontSize);
        Object[] drawings = words(text)
                .flatMap(word -> textImageFactory.createImages(word, fontSize).stream())
                .map(image -> imagePart(image, imagePartCache))
                .map(this::inline)
                .map(this::drawing)
                .map(this::r)
                .toArray();
        return pCentered(drawings);
    }

    private int getBodyWidthTwips() {
        float marginSides = metadata.getPaperbackMarginSides();
        float pageWidthInches = metadata.getPaperbackPageWidthInches();
        float bodyWidthInches = pageWidthInches - (2 * marginSides);
        log.info("Widths: page %.2f\", margins %.2f\", body %.2f\"".formatted(
                pageWidthInches, marginSides, bodyWidthInches
        ));
        return UnitsOfMeasurement.inchToTwip(bodyWidthInches);
    }

    private ImagePartCache getImagePartCache(FontSize fontSize) {
        return imagePartCaches.computeIfAbsent(fontSize,
                (fs) -> ImagePartCache.create());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ImagePartCache
            implements Map<String, BinaryPartAbstractImage> {

        @Delegate
        Map<String, BinaryPartAbstractImage> delegate = new HashMap<>();

        public static ImagePartCache create() {
            return new ImagePartCache();
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
                                .createImagePart(mlPackage, image.getFile());
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

    private Object drawing(Inline inline) {
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
            return imagePart
                    .createImageInline(
                            imagePart.getPartName().getName(),
                            imagePart.getContentType(),
                            idCounter.incrementAndGet(),
                            idCounter.incrementAndGet(),
                            getBodyWidthTwips(),
                            false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public P tocItem(String pageNumber, String title) {
        return p(
                tabDefinition(
                        tabs(
                                tabLeft(0),
                                tabRight(576),
                                tabLeft(720)
                        ),
                        tabIndent(720, null, 720)),
                r(
                        tab(),
                        t(pageNumber),
                        tab(),
                        t(title)
                )
        );
    }

    private PPrBase.Ind tabIndent(
            @Nullable Integer left,
            @Nullable Integer right,
            int hanging
    ) {
        PPrBase.Ind ind = objectFactory.createPPrBaseInd();
        if (left != null) ind.setLeft(BigInteger.valueOf(left));
        if (right != null) ind.setRight(BigInteger.valueOf(right));
        ind.setHanging(BigInteger.valueOf(hanging));
        return ind;
    }

    private Tabs tabs(CTTabStop... positions) {
        Tabs tabs = objectFactory.createTabs();
        tabs.getTab().addAll(Arrays.asList(positions));
        return tabs;
    }

    private Object tabDefinition(Tabs tabs, PPrBase.Ind tabIndent) {
        return ppr(tabs, tabIndent);
    }

    private CTTabStop tabLeft(int position) {
        CTTabStop tabStop = objectFactory.createCTTabStop();
        tabStop.setPos(BigInteger.valueOf(position));
        tabStop.setVal(STTabJc.LEFT);
        return tabStop;
    }

    private CTTabStop tabRight(int position) {
        CTTabStop tabStop = objectFactory.createCTTabStop();
        tabStop.setPos(BigInteger.valueOf(position));
        tabStop.setVal(STTabJc.RIGHT);
        return tabStop;
    }

    private Object tab() {
        return objectFactory.createRTab();
    }

    private PPr ppr(SectPr sectPr) {
        PPr pPr = objectFactory.createPPr();
        pPr.setSectPr(sectPr);
        return pPr;
    }

    private PPr ppr(Jc jc) {
        PPr pPr = objectFactory.createPPr();
        pPr.setJc(jc);
        return pPr;
    }

    private Object ppr(Tabs tabs, PPrBase.Ind tabIndent) {
        PPr pPr = objectFactory.createPPr();
        pPr.setTabs(tabs);
        pPr.setInd(tabIndent);
        return pPr;
    }

    private SectPr sectPr(SectPr.Type type) {
        SectPr sectPr = objectFactory.createSectPr();
        sectPr.setPgSz(pgSz());
        //TODO: MUST ALSO SATISFY THIS WAY OF GETTING PAGE SIZE:
        /**
         *
         List<SectionWrapper> sections = wmlPackage.getDocumentModel().getSections();
         PageDimensions page = sections.get(sections.size() - 1).getPageDimensions();
         */
        sectPr.setPgMar(pgMar());
        sectPr.setType(type);
        return sectPr;
    }

    private SectPr.PgMar pgMar() {
        SectPr.PgMar pgMar = objectFactory.createSectPrPgMar();
        BigInteger sides = BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata.getPaperbackMarginSides()));
        BigInteger topBottom = BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata.getPaperbackMarginTopBottom()));
        pgMar.setTop(topBottom);
        pgMar.setBottom(topBottom);
        pgMar.setLeft(sides);
        pgMar.setRight(sides);
        return pgMar;
    }

    private SectPr.PgSz pgSz() {
        SectPr.PgSz pgSz = objectFactory.createSectPrPgSz();
        pgSz.setH(BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata.getPaperbackPageHeightInches())));
        pgSz.setW(BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata.getPaperbackPageWidthInches())));
        return pgSz;
    }

    private SectPr.Type sectPrType(String value) {
        SectPr.Type type = new SectPr.Type();
        type.setVal(value);
        return type;
    }

    public P textParagraph(String text) {
        return p(r(t(text)));
    }

    private P p(Object... o) {
        P p = objectFactory.createP();
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    private P pCentered(Object... o) {
        P p = objectFactory.createP();
        p.getContent().add(ppr(jc(JcEnumeration.LEFT)));
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    private Jc jc(JcEnumeration value) {
        Jc jc = objectFactory.createJc();
        jc.setVal(value);
        return jc;
    }

    private R r(Object... o) {
        R r = objectFactory.createR();
        r.getContent().addAll(Arrays.asList(o));
        return r;
    }

    private Text t(String value) {
        Text text = objectFactory.createText();
        text.setValue(value);
        return text;
    }

}