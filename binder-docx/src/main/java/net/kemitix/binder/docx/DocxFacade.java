package net.kemitix.binder.docx;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.convert.out.flatOpcXml.FlatOpcXmlCreator;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
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
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STFtnEdn;
import org.docx4j.wml.STTabJc;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Style;
import org.docx4j.wml.Tabs;
import org.docx4j.wml.Text;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.docx4j.jaxb.Context.*;

@ApplicationScoped
public class DocxFacade {

    private final Metadata metadata;

    @Getter
    private final WordprocessingMLPackage mlPackage;

    private final ObjectFactory factory = getWmlObjectFactory();
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

    public P finaliseTitlePage(Context context) {
        SectPr sectPr = sectPr(sectPrType("oddPage"));
        if (context.hasHeader()) {
            addDefaultPageHeader(sectPr, context.getName(), p());
        } else {
            addBlankPageHeader(sectPr, context.getName());
        }
        if (context.hasFooter()) {
            addDefaultPageFooter(sectPr, context.getName(), p());
        } else {
            addBlankPageFooter(sectPr, context.getName());
        }
        return p(ppr(sectPr));
    }

    public P finaliseSection(Context context) {
        SectPr sectPr = sectPr(sectPrType("oddPage"));
        String name = context.getName();
        String title = context.getTitle();
        if (context.hasHeader()) {
            addEvenPageHeader(sectPr, name, textParagraphCentered(title));
            addDefaultPageHeader(sectPr, name,
                    textParagraphCentered("%s Issue %s"
                            .formatted(
                                    metadata.getTitle(),
                                    metadata.getIssue())));
        } else {
            addBlankPageHeader(sectPr, context.getName());
        }
        if (context.hasFooter()) {
            P pageNumberPlaceholder = alignCenter(p(pageNumberPlaceholder()));
            addEvenPageFooter(sectPr, name, pageNumberPlaceholder);
            addDefaultPageFooter(sectPr, name, pageNumberPlaceholder);
        } else {
            addBlankPageFooter(sectPr, context.getName());
        }
        return p(ppr(sectPr));
    }

    private void addBlankPageHeader(SectPr sectPr, String name) {
        P[] emptyP = {};
        addPageHeader(sectPr, name, HdrFtrRef.DEFAULT, emptyP);
        addPageHeader(sectPr, name, HdrFtrRef.EVEN, emptyP);
        addPageHeader(sectPr, name, HdrFtrRef.FIRST, emptyP);
    }

    private void addBlankPageFooter(SectPr sectPr, String name) {
        P[] emptyP = {};
        addPageFooter(sectPr, name, HdrFtrRef.DEFAULT, emptyP);
        addPageFooter(sectPr, name, HdrFtrRef.EVEN, emptyP);
        addPageFooter(sectPr, name, HdrFtrRef.FIRST, emptyP);
    }

    @SneakyThrows
    public void addStyle(Style style) {
        var part = mlPackage.getMainDocumentPart()
                .getStyleDefinitionsPart(true);
        List<Style> styles = part.getContents().getStyle();
        styles.add(style);
    }

    public R[] pageNumberPlaceholder() {
        //    <w:r>
        //      <w:rPr/>
        //      <w:fldChar w:fldCharType="begin"/>
        //    </w:r>
        //    <w:r>
        //      <w:rPr/>
        //      <w:instrText> PAGE </w:instrText>
        //    </w:r>
        //    <w:r>
        //      <w:rPr/>
        //      <w:fldChar w:fldCharType="separate"/>
        //    </w:r>
        //    <w:r>
        //      <w:rPr/>
        //      <w:t>2</w:t>
        //    </w:r>
        //    <w:r>
        //      <w:rPr/>
        //      <w:fldChar w:fldCharType="end"/>
        //    </w:r>
        FldChar begin = factory.createFldChar();
        begin.setFldCharType(STFldCharType.BEGIN);

        return new R[]{
                r(fldChar(STFldCharType.BEGIN)),
                r(instrText(" PAGE ")),
                r(fldChar(STFldCharType.SEPARATE)),
                r(t("2")),
                r(fldChar(STFldCharType.END))
        };
    }

    private JAXBElement<Text> instrText(String text) {
        return factory.createRInstrText(t(text));
    }

    private RPr rPr(R r) {
        RPr rPr = Objects.requireNonNullElseGet(
                r.getRPr(),
                factory::createRPr
        );
        r.setRPr(rPr);
        return rPr;
    }

    private FldChar fldChar(STFldCharType type) {
        FldChar fldChar = factory.createFldChar();
        fldChar.setFldCharType(type);
        return fldChar;
    }

    private MainDocumentPart mainDocumentPart() {
        return mlPackage.getMainDocumentPart();
    }

    public P tocItem(String pageNumber, String title) {
        return p(new Object[]{
                tabDefinition(
                        tabs(new CTTabStop[]{
                                tabLeft(0),
                                tabRight(576),
                                tabLeft(720)
                        }),
                        tabIndent(720, null, 720)),
                r(new Object[]{
                        tab(),
                        t(pageNumber),
                        tab(),
                        t(title)
                })
        });
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

    private Tabs tabs(CTTabStop[] positions) {
        Tabs tabs = factory.createTabs();
        tabs.getTab().addAll(Arrays.asList(positions));
        return tabs;
    }

    private PPr tabDefinition(Tabs tabs, PPrBase.Ind tabIndent) {
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

    private R.Tab tab() {
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

    private PPr ppr(Tabs tabs, PPrBase.Ind tabIndent) {
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
        return alignCenter(p(r(t(text))));
    }

    public P p(PPr pPr) {
        P p = factory.createP();
        p.setPPr(pPr);
        return p;
    }

    public P p() {
        return p(new Object[]{});
    }

    public P p(Object o) {
        return p(new Object[]{o});
    }

    public P p(Object[] o) {
        P p = factory.createP();
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    public P alignFull(P p) {
        pPr(p).setJc(jc(JcEnumeration.BOTH));
        return p;
    }

    public P alignLeft(P p) {
        pPr(p).setJc(jc(JcEnumeration.LEFT));
        return p;
    }

    public P alignRight(P p) {
        pPr(p).setJc(jc(JcEnumeration.RIGHT));
        return p;
    }

    public P alignCenter(P p) {
        pPr(p).setJc(jc(JcEnumeration.CENTER));
        return p;
    }

    public PPr pPr(P p) {
        PPr pPr = Objects.requireNonNullElseGet(
                p.getPPr(),
                factory::createPPr
        );
        p.setPPr(pPr);
        return pPr;
    }

    private Jc jc(JcEnumeration value) {
        Jc jc = factory.createJc();
        jc.setVal(value);
        return jc;
    }

    public R r() {
        return r(new Object[]{});
    }

    public R r(Object o) {
        return r(new Object[]{o});
    }

    public R r(Object[] o) {
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
        List<Object> content = r.getContent();
        Collections.addAll(content, drawings);
        return alignCenter(p(r));
    }

    public List<P> leaders() {
        return Arrays.asList(
                textParagraph(""),
                textParagraph("")
        );
    }

    public R italic(Object[] content) {
        RPr rPr = factory.createRPr();
        rPr.setI(factory.createBooleanDefaultTrue());
        List<Object> o = new ArrayList<>();
        o.add(rPr);
        o.addAll(Arrays.asList(content));
        return r(o.toArray());
    }

    public R bold(Object[] content) {
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
    public P heading(int level, String text) {
        R r = r(t(text));
        RPr rPr = rPr(r);
        HpsMeasure sz = factory.createHpsMeasure();
        sz.setVal(BigInteger.valueOf(szForLevel(level)));
        rPr.setSz(sz);
        rPr.setSzCs(sz);

        P p = p(r);
        PPr pPr = pPr(p);

        ParaRPr paraRPr = factory.createParaRPr();
        paraRPr.setSz(sz);
        paraRPr.setSzCs(sz);
        pPr.setRPr(paraRPr);

        PPrBase.PStyle pStyle = pStyle("Normal");
        pPr.setPStyle(pStyle);

        return p;
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
    public P bulletItem(String text) {
        P p = p(r(t(text)));

        PPr pPr = pPr(p);
        pPr.setPStyle(pStyle("Normal"));

        PPrBase.NumPr.NumId numId = factory.createPPrBaseNumPrNumId();
        numId.setVal(BigInteger.TWO);
        PPrBase.NumPr numPr = factory.createPPrBaseNumPr();
        PPrBase.NumPr.Ilvl ilvl = factory.createPPrBaseNumPrIlvl();
        ilvl.setVal(BigInteger.ZERO);
        numPr.setIlvl(ilvl);
        numPr.setNumId(numId);
        pPr.setNumPr(numPr);

        return p;
    }

    public R footnote(String ordinal, List<P> footnoteBody) {
        //TODO add footnote bodies from FootnoteBlockDocxNodeHandler
        // this will provide styles footnotes
        return footnoteReference(footnoteBody);
    }

    @SneakyThrows
    private R footnoteReference(List<P> footnoteBody) {
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
        JAXBElement<CTFtnEdnRef> footnoteReference = factory.createRFootnoteReference(ctFtnEdnRef);

        String footnoteOrdinal = ctFtnEdn.getId().toString();

        R r = r(new Object[]{
                footnoteReference,
                t(footnoteOrdinal)
        });

        RPr rPr = rPr(r);
        rPr.setRStyle(rStyle("FootnoteAnchor"));

        return r;
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

        List<Object> objects = new ArrayList<>();
        objects.add(pPr);
        R r = r(
                factory.createRFootnoteRef()
        );
        RPr rPr = rPr(r);
        rPr.setRStyle(rStyle("FootnoteCharacters"));

        objects.add(r);
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
    public P keepWithNext(P p) {
        PPr pPr = Objects.requireNonNullElseGet(p.getPPr(), factory::createPPr);
        pPr.setKeepNext(factory.createBooleanDefaultTrue());
        p.setPPr(pPr);
        return p;
    }

    public P keepTogether(P p) {
        PPr pPr = Objects.requireNonNullElseGet(p.getPPr(), factory::createPPr);
        pPr.setKeepLines(factory.createBooleanDefaultTrue());
        p.setPPr(pPr);
        return p;
    }

    @SneakyThrows
    public void addDefaultPageFooter(
            SectPr sectPr,
            String name,
            P pageFooter
    ) {
        addPageFooter(sectPr, name, HdrFtrRef.DEFAULT, new P[]{
                p(), pageFooter
        });
    }

    @SneakyThrows
    public void addEvenPageFooter(
            SectPr sectPr,
            String name,
            P pageFooter
    ) {
        addPageFooter(sectPr, name, HdrFtrRef.EVEN, new P[]{
                p(), pageFooter
        });
    }

    @SneakyThrows
    public void addFirstPageFooter(
            SectPr sectPr,
            String name,
            P pageFooter
    ) {
        addPageFooter(sectPr, name, HdrFtrRef.FIRST, new P[]{
                p(), pageFooter
        });
    }

    @SneakyThrows
    public void addDefaultPageHeader(
            SectPr sectPr,
            String name,
            P pageHeader
    ) {
        addPageHeader(sectPr, name, HdrFtrRef.DEFAULT, new P[]{
                pageHeader, p()
        });
    }

    @SneakyThrows
    public void addEvenPageHeader(
            SectPr sectPr,
            String name,
            P pageHeader
    ) {
        addPageHeader(sectPr, name, HdrFtrRef.EVEN, new P[]{
                pageHeader, p()
        });
    }

    @SneakyThrows
    public void addFirstPageHeader(
            SectPr sectPr,
            String name,
            P pageHeader
    ) {
        addPageHeader(sectPr, name, HdrFtrRef.FIRST, new P[]{
                pageHeader, p()
        });
    }

    @SneakyThrows
    public void addPageFooter(
            SectPr sectPr,
            String name,
            HdrFtrRef hdrFtrRef,
            P[] footerContent
    ) {
        FooterPart footerPart = new FooterPart();
        PartName partName = new PartName("/word/footer-%s-%s.xml".formatted(
                hdrFtrRef.value(), name));
        footerPart.setPartName(partName);
        Relationship relationship = mainDocumentPart().addTargetPart(footerPart);
        Ftr ftr = factory.createFtr();
        footerPart.setJaxbElement(ftr);
        ftr.getContent().addAll(Arrays.asList(footerContent));
        FooterReference footerReference = factory.createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(hdrFtrRef);
        sectPr.getEGHdrFtrReferences().add(footerReference);
    }

    @SneakyThrows
    public void addPageHeader(
            SectPr sectPr,
            String name,
            HdrFtrRef hdrFtrRef,
            P[] headerContent
    ) {
        HeaderPart headerPart = new HeaderPart();
        PartName partName = new PartName("/word/header-%s-%s.xml".formatted(
                hdrFtrRef.value(), name));
        headerPart.setPartName(partName);
        Relationship relationship = mainDocumentPart().addTargetPart(headerPart);
        Hdr hdr = factory.createHdr();
        headerPart.setJaxbElement(hdr);
        hdr.getContent().addAll(Arrays.asList(headerContent));
        //hdr.getContent().add(p());
        HeaderReference headerReference = factory.createHeaderReference();
        headerReference.setId(relationship.getId());
        headerReference.setType(hdrFtrRef);
        sectPr.getEGHdrFtrReferences().add(headerReference);
    }

    @SneakyThrows
    public void dump() {
        FlatOpcXmlCreator worker = new FlatOpcXmlCreator(mlPackage);
        File file = new File("binder-pkg.xml");
        worker.marshal(new PrintStream(file));
        System.out.println("Wrote: " + file.getAbsolutePath());
    }

    public P fontSzP(int fontSize, P p) {
        fontSzPPr(fontSize, pPr(p));
        return p;
    }

    // This isn't setting the font size for the para!!
    public PPr fontSzPPr(int fontSize, PPr pPr) {
        fontSzParaRPr(fontSize, rPr(pPr));
        return pPr;
    }

    public ParaRPr fontSzParaRPr(int fontSize, ParaRPr rPr) {
        HpsMeasure sz = sz(fontSize);
        rPr.setSz(sz);
        rPr.setSzCs(sz);
        return rPr;
    }

    public HpsMeasure sz(int fontSize) {
        HpsMeasure hpsMeasure = factory.createHpsMeasure();
        hpsMeasure.setVal(BigInteger.valueOf(fontSize * 2));
        return hpsMeasure;
    }

    public ParaRPr rPr(PPr pPr) {
        ParaRPr rPr = Objects.requireNonNullElseGet(
                pPr.getRPr(),
                factory::createParaRPr
        );
        pPr.setRPr(rPr);
        return rPr;
    }

    public Style createParaStyleBasedOn(String name, String basedOn) {
        Style style = named(name, basedOn(basedOn, createStyle(name)));
        style.setType("paragraph");
        return style;
    }

    private Style named(String name, Style style) {
        Style.Name styleName = factory.createStyleName();
        styleName.setVal(name);
        style.setName(styleName);
        return style;
    }

    private Style basedOn(String name, Style style) {
        Style.BasedOn basedOn = factory.createStyleBasedOn();
        basedOn.setVal(name);
        style.setBasedOn(basedOn);
        return style;
    }

    public Style createCharStyleBasedOn(String name, String basedOn) {
        Style style = named(name, basedOn(basedOn, createStyle(name)));
        style.setType("character");
        return style;
    }

    private Style createStyle(String name) {
        Style style = factory.createStyle();
        style.setStyleId(name);
        return style;
    }

    public Style fontSize(int fontSize, Style style) {
        RPr rPr = rPr(style);
        rPr.setSz(sz(fontSize));
        rPr.setSzCs(sz(fontSize));
        return style;
    }

    private RPr rPr(Style style) {
        RPr rPr = Objects.requireNonNullElseGet(
                style.getRPr(),
                factory::createRPr
        );
        style.setRPr(rPr);
        return rPr;
    }

    public P styledP(String styleName, P p) {
        PPr pPr = pPr(p);

        PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
        pPr.setPStyle(pStyle);
        pStyle.setVal(styleName);

        return p;
    }
}
