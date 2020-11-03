package net.kemitix.binder.docx;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.STTabJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Tabs;
import org.docx4j.wml.Text;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class DocxFacade {

    private final Metadata metadata;

    @Getter
    private final WordprocessingMLPackage mlPackage;

    private final ObjectFactory objectFactory =
            Context.getWmlObjectFactory();
    private final AtomicInteger myFootnoteRef = new AtomicInteger(1);

    @Inject
    public DocxFacade(
            Metadata metadata
    ) throws InvalidFormatException {
        this.metadata = metadata;

        mlPackage = WordprocessingMLPackage.createPackage();
    }

    public P breakToOddPage() {
        return p(ppr(sectPr(sectPrType("oddPage"))));
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

    public P textParagraphCentered(String text) {
        return pCentered(r(t(text)));
    }

    public P p(Object... o) {
        P p = objectFactory.createP();
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    private P pCentered(Object... o) {
        P p = objectFactory.createP();
        p.getContent().add(ppr(jc(JcEnumeration.CENTER)));
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    private Jc jc(JcEnumeration value) {
        Jc jc = objectFactory.createJc();
        jc.setVal(value);
        return jc;
    }

    public R r(Object... o) {
        R r = objectFactory.createR();
        r.getContent().addAll(Arrays.asList(o));
        return r;
    }

    public Text t(String value) {
        Text text = objectFactory.createText();
        text.setValue(value);
        text.setSpace("preserve");// contains significant whitespace
        return text;
    }

    public P drawings(Drawing[] drawings) {
        R r = r();
        for (Drawing drawing : drawings) {
            r.getContent().add(drawing);
        }
        return pCentered(r);
    }

    public List<P> leaders() {
        return Arrays.asList(
                textParagraph(""),
                textParagraph("")
        );
    }

    public Object italic(Object... content) {
        RPr rPr = objectFactory.createRPr();
        rPr.setI(objectFactory.createBooleanDefaultTrue());
        List<Object> o = new ArrayList<>();
        o.add(rPr);
        o.addAll(Arrays.asList(content));
        return r(o.toArray());
    }

    public Object bold(Object... content) {
        RPr rPr = objectFactory.createRPr();
        rPr.setB(objectFactory.createBooleanDefaultTrue());
        List<Object> o = new ArrayList<>();
        o.add(rPr);
        o.addAll(Arrays.asList(content));
        return r(o.toArray());
    }

    /**
     * <w:p>
     *   <w:pPr>
     *     <w:pStyle w:val="Normal"/>
     *     <w:rPr>
     *       <w:sz w:val="120"/>
     *       <w:szCs w:val="120"/>
     *     </w:rPr>
     *   </w:pPr>
     *   <w:r>
     *     <w:rPr>
     *       <w:sz w:val="120"/>
     *       <w:szCs w:val="120"/>
     *     </w:rPr>
     *     <w:t>HEADING 2</w:t>
     *   </w:r>
     * </w:p>
     */
    public Object heading(int level, String text) {
        PPr pPr = objectFactory.createPPr();

        PPrBase.PStyle pStyle = pStyle("Normal");
        pPr.setPStyle(pStyle);

        HpsMeasure sz = objectFactory.createHpsMeasure();
        sz.setVal(BigInteger.valueOf(szForLevel(level)));

        RPr rPr = objectFactory.createRPr();
        rPr.setSz(sz);
        rPr.setSzCs(sz);

        ParaRPr paraRPr = objectFactory.createParaRPr();
        paraRPr.setSz(sz);
        paraRPr.setSzCs(sz);
        pPr.setRPr(paraRPr);
        return
                p(
                        pPr,
                        r(
                                rPr,
                                t(text)
                        )
                );
    }

    // h1 = 24pt
    // h2 = 18pt
    // h3 = 12pt
    // lvl => 30 - (6 * lvl)
    private long szForLevel(int level) {
        int point = 30 - (6 * level);
        return point * 2;
    }

    /**
     * <w:p>
     *   <w:pPr>
     *     <w:pStyle w:val="Normal"/>
     *     <w:numPr>
     *       <w:ilvl w:val="0"/>
     *       <w:numId w:val="1"/>
     *     </w:numPr>
     *     <w:rPr/>
     *   </w:pPr>
     *   <w:r>
     *     <w:rPr/>
     *     <w:t>item 1</w:t>
     *   </w:r>
     * </w:p>
     */
    public Object bulletItem(String text) {
        PPr pPr = objectFactory.createPPr();
        pPr.setPStyle(pStyle("Normal"));

        PPrBase.NumPr.Ilvl ilvl = objectFactory.createPPrBaseNumPrIlvl();
        ilvl.setVal(BigInteger.ZERO);

        PPrBase.NumPr numPr = objectFactory.createPPrBaseNumPr();
        numPr.setIlvl(ilvl);

        PPrBase.NumPr.NumId numId = objectFactory.createPPrBaseNumPrNumId();
        numId.setVal(BigInteger.TWO);
        numPr.setNumId(numId);

        pPr.setNumPr(numPr);

        pPr.setRPr(objectFactory.createParaRPr());

        RPr rPr = objectFactory.createRPr();
        return
                p(
                        pPr,
                        r(
                                rPr,
                                t(text)
                        )
                );
    }

    public Object footnote(String id, String footnoteBody) {
        // in document.xml:
        //      <w:r>
        //        <w:rPr>
        //          <w:rStyle w:val="FootnoteAnchor"/>
        //        </w:rPr>
        //        <w:footnoteReference w:id="2"/>
        //      </w:r>
        return footnoteReference(footnoteBody);
    }

    @SneakyThrows
    private Object footnoteReference(String footnoteBody) {
        FootnotesPart footnotesPart = getFootnotesPart();
        CTFootnotes contents = footnotesPart.getContents();
        BigInteger myId = BigInteger.valueOf(myFootnoteRef.getAndIncrement());
        List<CTFtnEdn> footnotes = contents.getFootnote();
        CTFtnEdn ctFtnEdn = footnotes.stream()
                .filter(o -> o.getId().equals(myId))
                .findFirst()
                .orElseGet(() -> {
                    CTFtnEdn edn = objectFactory.createCTFtnEdn();
                    footnotes.add(edn);
                    return edn;
                });
        ctFtnEdn.getContent().add(footnoteBody(footnoteBody));
        ctFtnEdn.setId(myId);
        CTFtnEdnRef ctFtnEdnRef = objectFactory.createCTFtnEdnRef();
        ctFtnEdnRef.setId(myId);

        RPr rPr = objectFactory.createRPr();
        rPr.setRStyle(rStyle("FootnoteAnchor"));
        return r(
                rPr,
                objectFactory.createRFootnoteReference(ctFtnEdnRef),
                t(myId.toString())
        );
    }

    private FootnotesPart getFootnotesPart() {
        MainDocumentPart mainDocumentPart = mlPackage.getMainDocumentPart();
        return Objects.requireNonNullElseGet(
                mainDocumentPart.getFootnotesPart(),
                () -> {
                    try {
                        FootnotesPart part = new FootnotesPart();
                        part.setContents(objectFactory.createCTFootnotes());
                        mainDocumentPart.addTargetPart(part);
                        return part;
                    } catch (InvalidFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private Object footnoteBody(String footnoteBody) {
        // in footnotes.xml:
        //  <w:footnote w:id="2">
        //    <w:p>
        //      <w:pPr>
        //        <w:pStyle w:val="Footnote"/>
        //        <w:rPr/>
        //      </w:pPr>
        //      <w:r>
        //        <w:rPr>
        //          <w:rStyle w:val="FootnoteCharacters"/>
        //        </w:rPr>
        //        <w:footnoteRef/>
        //      </w:r>
        //      <w:r>
        //        <w:rPr/>
        //        <w:tab/>
        //        <w:t>Footnote</w:t>
        //      </w:r>
        //    </w:p>
        //  </w:footnote>
        PPr pPr = objectFactory.createPPr();
        pPr.setPStyle(pStyle("Footnote"));

        RPr rPr = objectFactory.createRPr();
        rPr.setRStyle(rStyle("FootnoteCharacters"));
        return
                p(
                        pPr,
                        r(
                                rPr,
                                objectFactory.createRFootnoteRef()
                        ),
                        r(
                                objectFactory.createRPr(),
                                objectFactory.createRTab(),
                                t(
                                        //TODO - handle para splits: "~PARA~"
                                        footnoteBody
                                )
                        )
                );
    }

    private PPrBase.PStyle pStyle(String val) {
        var pStyle = objectFactory.createPPrBasePStyle();
        pStyle.setVal(val);
        return pStyle;
    }

    private RStyle rStyle(String val) {
        var rStyle = objectFactory.createRStyle();
        rStyle.setVal(val);
        return rStyle;
    }
}