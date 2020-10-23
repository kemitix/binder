package net.kemitix.binder.docx;

import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.Drawing;
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
import java.util.List;

@ApplicationScoped
public class DocxHelper {

    private final ObjectFactory objectFactory;
    private final WordprocessingMLPackage mlPackage;
    private final TextImageFactory textImageFactory;
    private final Metadata metadata;

    @Inject
    public DocxHelper(
            ObjectFactory objectFactory,
            WordprocessingMLPackage mlPackage,
            TextImageFactory textImageFactory,
            Metadata metadata
    ) {
        this.objectFactory = objectFactory;
        this.mlPackage = mlPackage;
        this.textImageFactory = textImageFactory;
        this.metadata = metadata;
    }

    public P breakToOddPage() {
        return p(ppr(sectPr(sectPrType("oddPage"))));
    }

    public P textImage(String text, int fontSize, int pageWidth) {
        List<TextImage> images = textImageFactory.createImages(text, fontSize, pageWidth);

        Object[] drawings = images.stream()
                .map(TextImage::getBytes)
                .map(this::drawing)
                .toArray();
        return pCentered(r(drawings));
    }

    private Object drawing(byte[] bytes) {
        try {
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(mlPackage, bytes);
            Inline inline = imagePart.createImageInline("hint", "", 0, 0, false);
            Drawing drawing = objectFactory.createDrawing();
            drawing.getAnchorOrInline().add(inline);
            return drawing;
        } catch (Exception e) {
            e.printStackTrace();
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
        sectPr.setPgMar(pgMar());
        sectPr.setType(type);
        return sectPr;
    }

    private SectPr.PgMar pgMar() {
        SectPr.PgMar pgMar = objectFactory.createSectPrPgMar();
        double ratio = getInchesToUnitsRatio();
        float paperbackMarginSides = metadata.getPaperbackMarginSides();
        float paperbackMarginTopBottom = metadata.getPaperbackMarginTopBottom();
        BigInteger sides = BigInteger.valueOf((long) (paperbackMarginSides * ratio));
        BigInteger topBottom = BigInteger.valueOf((long) (paperbackMarginTopBottom * ratio));
        pgMar.setTop(topBottom);
        pgMar.setBottom(topBottom);
        pgMar.setLeft(sides);
        pgMar.setRight(sides);
        return pgMar;
    }

    private SectPr.PgSz pgSz() {
        SectPr.PgSz pgSz = objectFactory.createSectPrPgSz();
        float widthInches = metadata.getPaperbackPageWidthInches();
        float heightInches = metadata.getPaperbackPageHeightInches();
        double ratio = getInchesToUnitsRatio();
        long width = (long) (widthInches * ratio);
        long height = (long) (heightInches * ratio);
        pgSz.setH(BigInteger.valueOf(height));
        pgSz.setW(BigInteger.valueOf(width));
        return pgSz;
    }

    private double getInchesToUnitsRatio() {
        // A4: 8.3" x 11.7"
        //        pgSz.setW(BigInteger.valueOf(11907));
        //        pgSz.setH(BigInteger.valueOf(16839));
        double ratio = 11907 / 8.3;
        return ratio;
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