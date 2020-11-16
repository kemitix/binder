package net.kemitix.binder.docx;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
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
import org.docx4j.wml.STFtnEdn;
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
import java.util.stream.Collectors;

@ApplicationScoped
public class DocxFacade {

    private final Metadata metadata;

    @Getter
    private final WordprocessingMLPackage mlPackage;

    private final ObjectFactory factory = Context.getWmlObjectFactory();
    private final AtomicInteger myFootnoteRef = new AtomicInteger(0);

    @Inject
    public DocxFacade(
            Metadata metadata
    ) throws InvalidFormatException {
        this.metadata = metadata;

        mlPackage = WordprocessingMLPackage.createPackage();
        SectPr sectPr = mlPackage.getDocumentModel().getSections().get(0).getSectPr();
        sectPr.setPgSz(pgSz());
        sectPr.setPgMar(pgMar());
    }

    public P breakToOddPage() {
        return p(ppr(sectPr(sectPrType("oddPage"))));
    }

    public SectPr getSectPr(P p) {
        return p.getPPr().getSectPr();
    }

    @SneakyThrows
    public void addDefaultPageFooter(
            SectPr sectPr,
            String text
    ) {
        FooterPart footerPart = new FooterPart();
        Relationship relationship = mainDocumentPart().addTargetPart(footerPart);
        Ftr ftr = factory.createFtr();
        footerPart.setJaxbElement(ftr);
        ftr.getContent()
                .add(textParagraphCentered(text));
        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(HdrFtrRef.DEFAULT);
        sectPr.getEGHdrFtrReferences().add(footerReference);
    }

    private MainDocumentPart mainDocumentPart() {
        return mlPackage.getMainDocumentPart();
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
        PPrBase.Ind ind = factory.createPPrBaseInd();
        if (left != null) ind.setLeft(BigInteger.valueOf(left));
        if (right != null) ind.setRight(BigInteger.valueOf(right));
        ind.setHanging(BigInteger.valueOf(hanging));
        return ind;
    }

    private Tabs tabs(CTTabStop... positions) {
        Tabs tabs = factory.createTabs();
        tabs.getTab().addAll(Arrays.asList(positions));
        return tabs;
    }

    private Object tabDefinition(Tabs tabs, PPrBase.Ind tabIndent) {
        return ppr(tabs, tabIndent);
    }

    private CTTabStop tabLeft(int position) {
        CTTabStop tabStop = factory.createCTTabStop();
        tabStop.setPos(BigInteger.valueOf(position));
        tabStop.setVal(STTabJc.LEFT);
        return tabStop;
    }

    private CTTabStop tabRight(int position) {
        CTTabStop tabStop = factory.createCTTabStop();
        tabStop.setPos(BigInteger.valueOf(position));
        tabStop.setVal(STTabJc.RIGHT);
        return tabStop;
    }

    private Object tab() {
        return factory.createRTab();
    }

    private PPr ppr(SectPr sectPr) {
        PPr pPr = pPr();
        pPr.setSectPr(sectPr);
        return pPr;
    }

    private PPr pPr() {
        return factory.createPPr();
    }

    private PPr ppr(Jc jc) {
        PPr pPr = pPr();
        pPr.setJc(jc);
        return pPr;
    }

    private Object ppr(Tabs tabs, PPrBase.Ind tabIndent) {
        PPr pPr = pPr();
        pPr.setTabs(tabs);
        pPr.setInd(tabIndent);
        return pPr;
    }

    private SectPr sectPr(SectPr.Type type) {
        SectPr sectPr = factory.createSectPr();
        sectPr.setPgSz(pgSz());
        sectPr.setPgMar(pgMar());
        sectPr.setType(type);
        return sectPr;
    }

    private SectPr.PgMar pgMar() {
        SectPr.PgMar pgMar = factory.createSectPrPgMar();
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
        SectPr.PgSz pgSz = factory.createSectPrPgSz();
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

    public P p(PPr pPr) {
        P p = factory.createP();
        p.setPPr(pPr);
        return p;
    }

    public P p(Object... o) {
        P p = factory.createP();
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    private P pCentered(Object... o) {
        P p = factory.createP();
        p.getContent().add(ppr(jc(JcEnumeration.CENTER)));
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    private Jc jc(JcEnumeration value) {
        Jc jc = factory.createJc();
        jc.setVal(value);
        return jc;
    }

    public R r(Object... o) {
        R r = factory.createR();
        r.getContent().addAll(Arrays.asList(o));
        return r;
    }

    public Text t(String value) {
        Text text = factory.createText();
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
        RPr rPr = factory.createRPr();
        rPr.setI(factory.createBooleanDefaultTrue());
        List<Object> o = new ArrayList<>();
        o.add(rPr);
        o.addAll(Arrays.asList(content));
        return r(o.toArray());
    }

    public Object bold(Object... content) {
        RPr rPr = factory.createRPr();
        rPr.setB(factory.createBooleanDefaultTrue());
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
        PPr pPr = pPr();

        PPrBase.PStyle pStyle = pStyle("Normal");
        pPr.setPStyle(pStyle);

        HpsMeasure sz = factory.createHpsMeasure();
        sz.setVal(BigInteger.valueOf(szForLevel(level)));

        RPr rPr = factory.createRPr();
        rPr.setSz(sz);
        rPr.setSzCs(sz);

        ParaRPr paraRPr = factory.createParaRPr();
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
        PPr pPr = pPr();
        pPr.setPStyle(pStyle("Normal"));

        PPrBase.NumPr.Ilvl ilvl = factory.createPPrBaseNumPrIlvl();
        ilvl.setVal(BigInteger.ZERO);

        PPrBase.NumPr numPr = factory.createPPrBaseNumPr();
        numPr.setIlvl(ilvl);

        PPrBase.NumPr.NumId numId = factory.createPPrBaseNumPrNumId();
        numId.setVal(BigInteger.TWO);
        numPr.setNumId(numId);

        pPr.setNumPr(numPr);

        pPr.setRPr(factory.createParaRPr());

        RPr rPr = factory.createRPr();
        return
                p(
                        pPr,
                        r(
                                rPr,
                                t(text)
                        )
                );
    }

    public Object footnote(String ordinal, List<P> footnoteBody) {
        //TODO add footnote bodies from FootnoteBlockDocxNodeHandler
        // this will provide styles footnotes
        return footnoteReference(footnoteBody);
    }

    @SneakyThrows
    private Object footnoteReference(List<P> footnoteBody) {
        // in document.xml:
        //      <w:r>
        //        <w:rPr>
        //          <w:rStyle w:val="FootnoteAnchor"/>
        //        </w:rPr>
        //        <w:footnoteReference w:id="2"/>
        //      </w:r>
        FootnotesPart footnotesPart = getFootnotesPart();
        CTFootnotes contents = footnotesPart.getContents();
        List<CTFtnEdn> footnotes = contents.getFootnote();
        CTFtnEdn ctFtnEdn = getNextCtFtnEdn(footnotes);
        ctFtnEdn.getContent().addAll(Arrays.asList(footnoteBody(footnoteBody)));
        CTFtnEdnRef ctFtnEdnRef = factory.createCTFtnEdnRef();
        ctFtnEdnRef.setId(ctFtnEdn.getId());

        RPr rPr = factory.createRPr();
        rPr.setRStyle(rStyle("FootnoteAnchor"));
        return r(
                rPr,
                factory.createRFootnoteReference(ctFtnEdnRef),
                t(ctFtnEdn.getId().toString())
        );
    }

    private CTFtnEdn getNextCtFtnEdn(List<CTFtnEdn> footnotes) {
        var id = BigInteger.valueOf(myFootnoteRef.getAndIncrement());
        return footnotes.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    CTFtnEdn edn = factory.createCTFtnEdn();
                    edn.setId(id);
                    footnotes.add(edn);
                    return edn;
                });
    }

    private FootnotesPart getFootnotesPart() {
        return Objects.requireNonNullElseGet(
                mainDocumentPart().getFootnotesPart(),
                () -> {
                    try {
                        FootnotesPart part = new FootnotesPart();
                        part.setContents(initFootnotes());
                        mainDocumentPart().addTargetPart(part);
                        return part;
                    } catch (InvalidFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private CTFootnotes initFootnotes() {
        CTFootnotes ctFootnotes = factory.createCTFootnotes();
        List<CTFtnEdn> footnotes = ctFootnotes.getFootnote();

        //    <w:footnote w:id="0" w:type="separator">
        //        <w:p>
        //            <w:r>
        //                <w:separator/>
        //            </w:r>
        //        </w:p>
        //    </w:footnote>
        CTFtnEdn separator = getNextCtFtnEdn(footnotes);
        separator.setType(STFtnEdn.SEPARATOR);
        separator.getContent().add(p(r(factory.createRSeparator())));

//        //    <w:footnote w:id="1" w:type="continuationSeparator">
//        //        <w:p>
//        //            <w:r>
//        //                <w:continuationSeparator/>
//        //            </w:r>
//        //        </w:p>
//        //    </w:footnote>
//        CTFtnEdn continuation = getNextCtFtnEdn(footnotes);
//        continuation.setType(STFtnEdn.CONTINUATION_SEPARATOR);
//        continuation.getContent().add(p(r(objectFactory.createRContinuationSeparator())));

        return ctFootnotes;
    }

    private Object[] footnoteBody(List<P> footnoteParas) {
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
        PPr pPr = pPr();
        pPr.setPStyle(pStyle("Footnote"));

        RPr rPr = factory.createRPr();
        rPr.setRStyle(rStyle("FootnoteCharacters"));

        List<Object> objects = new ArrayList<>();
        objects.add(pPr);
        objects.add(r(
                rPr,
                factory.createRFootnoteRef()
        ));
        objects.addAll(
                footnoteParas.stream()
                        .peek(para -> para.setPPr(pPr)
        ).collect(Collectors.toList()));
        return objects.toArray();
    }

    private PPrBase.PStyle pStyle(String val) {
        var pStyle = factory.createPPrBasePStyle();
        pStyle.setVal(val);
        return pStyle;
    }

    private RStyle rStyle(String val) {
        var rStyle = factory.createRStyle();
        rStyle.setVal(val);
        return rStyle;
    }

    //p/pPr/keepNext[val="true"]
    public Object keepWithNext(P p) {
        PPr pPr = Objects.requireNonNullElseGet(p.getPPr(), factory::createPPr);
        pPr.setKeepNext(factory.createBooleanDefaultTrue());
        p.setPPr(pPr);
        return p;
    }

    public Object keepTogether(P p) {
        PPr pPr = Objects.requireNonNullElseGet(p.getPPr(), factory::createPPr);
        pPr.setKeepLines(factory.createBooleanDefaultTrue());
        p.setPPr(pPr);
        return p;
    }

    public Hdr createPageHeader(String title) {
        PPr pPr = pPr();
        pPr.setPStyle(pStyle("Header"));
        pPr.setJc(jc(JcEnumeration.CENTER));

        P p = p(r(t(title)));
        p.setPPr(pPr);

        Hdr hdr = factory.createHdr();
        hdr.getContent().add(p);

        

        return hdr;
    }

}
