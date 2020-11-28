package net.kemitix.binder.docx;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTColumns;
import org.docx4j.wml.CTDocGrid;
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.docx4j.jaxb.Context.getWmlObjectFactory;

@ApplicationScoped
public class DocxFacade
        implements DocxHeaderFootersFacadeMixIn {

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

    @Override
    public ObjectFactory factory() {
        return factory;
    }

    public List<P> finaliseTitlePage(
            Context context,
            List<Object> pageContent
    ) {
        // force into a list of P - we have an invalid section if this results in ClassCastException
        List<P> sectionPs = pageContent.stream()
                .map(P.class::cast)
                .collect(Collectors.toList());
        SectPr sectPr =
                formatSection(
                        sectPrType("oddPage",
                                sectPr(sectionPs)));
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
        return sectionPs;
    }

    public List<P> finaliseSection(
            Context context,
            List<Object> pageContent
    ) {
        // force into a list of P - we have an invalid section if this results in ClassCastException
        List<P> sectionPs = pageContent.stream()
                .map(P.class::cast)
                .collect(Collectors.toList());
        SectPr sectPr =
                formatSection(
                        sectPrType("oddPage",
                                sectPr(sectionPs)));
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
        return sectionPs;
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

    private <T, K> K get(
            T t,
            Function<T, K> extract,
            Consumer<K> apply,
            Supplier<K> create
    ) {
        K k = Objects.requireNonNullElseGet(
                extract.apply(t),
                create
        );
        apply.accept(k);
        return k;
    }

    private RPr rPr(R r) {
        return get(r, R::getRPr, r::setRPr, factory::createRPr);
    }

    private FldChar fldChar(STFldCharType type) {
        FldChar fldChar = factory.createFldChar();
        fldChar.setFldCharType(type);
        return fldChar;
    }

    public MainDocumentPart mainDocumentPart() {
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

    private PPr pPr() {
        return factory.createPPr();
    }

    private PPr ppr(Tabs tabs, PPrBase.Ind tabIndent) {
        PPr pPr = pPr();
        pPr.setTabs(tabs);
        pPr.setInd(tabIndent);
        return pPr;
    }

    private SectPr sectPr(List<P> sectionPs) {
        P last = last(sectionPs, P.class);
        PPr pPr = pPr(last);
        return get(pPr, PPr::getSectPr, pPr::setSectPr, factory::createSectPr);
    }

    private SectPr formatSection(SectPr sectPr) {
        sectPr.setPgSz(pgSz());
        sectPr.setPgMar(pgMar());

        CTColumns ctColumns = factory.createCTColumns();
        sectPr.setCols(ctColumns);
        BigInteger topBottom = BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata.getPaperbackMarginTopBottom()));
        ctColumns.setSpace(topBottom);

        CTDocGrid ctDocGrid = factory.createCTDocGrid();
        sectPr.setDocGrid(ctDocGrid);
        ctDocGrid.setLinePitch(topBottom);

        return sectPr;
    }

    private <T> T last(List<T> items, Class<? extends T> tClass) {
        return items.get(items.size() - 1);
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
        pgMar.setHeader(topBottom);
        pgMar.setFooter(topBottom);
        pgMar.setGutter(BigInteger.ZERO);
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

    private SectPr sectPrType(String value, SectPr sectPr) {
        get(sectPr, SectPr::getType, sectPr::setType, SectPr.Type::new)
                .setVal(value);
        return sectPr;
    }

    public P textParagraph(String text) {
        return p(r(t(text)));
    }

    public P textParagraphCentered(String text) {
        return alignCenter(p(r(t(text))));
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

    public P align(JcEnumeration alignment, P p) {
        pPr(p).setJc(jc(alignment));
        return p;
    }

    public P alignFull(P p) {
        return align(JcEnumeration.BOTH, p);
    }

    public P alignLeft(P p) {
        return align(JcEnumeration.LEFT, p);
    }

    public P alignRight(P p) {
        return align(JcEnumeration.RIGHT, p);
    }

    public P alignCenter(P p) {
        return align(JcEnumeration.CENTER, p);
    }

    public PPr pPr(P p) {
        return get(p, P::getPPr, p::setPPr, factory::createPPr);
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

    public R italic(R r) {
        rPr(r).setI(defaultTrue());
        return r;
    }

    public R bold(R r) {
        rPr(r).setB(defaultTrue());
        return r;
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
        P p = p(r);
        PPr pPr = pPr(p);
        ParaRPr paraRPr = paraRPr(pPr);
        RPr rPr = rPr(r);

        pPr.setPStyle(pStyle("Normal"));

        HpsMeasure sz = factory.createHpsMeasure();
        sz.setVal(BigInteger.valueOf(szForLevel(level)));

        rPr.setSz(sz);
        rPr.setSzCs(sz);

        paraRPr.setSz(sz);
        paraRPr.setSzCs(sz);

        return p;
    }

    private ParaRPr paraRPr(PPr pPr) {
        return get(pPr, PPr::getRPr, pPr::setRPr, factory::createParaRPr);
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

    private BooleanDefaultTrue defaultTrue() {
        return factory.createBooleanDefaultTrue();
    }

    //p/pPr/keepNext[val="true"]
    public P keepWithNext(P p) {
        pPr(p).setKeepNext(defaultTrue());
        return p;
    }

    public P keepTogether(P p) {
        pPr(p).setKeepLines(defaultTrue());
        return p;
    }


    private P styleP(String styleName, P p) {
        pPr(p).setPStyle(pStyle(styleName));
        return p;
    }

    public HpsMeasure sz(float fontSize) {
        HpsMeasure hpsMeasure = factory.createHpsMeasure();
        hpsMeasure.setVal(BigInteger.valueOf((long) (fontSize * 2)));
        return hpsMeasure;
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

    public Style fontSize(float fontSize, Style style) {
        RPr rPr = rPr(style);
        rPr.setSz(sz(fontSize));
        rPr.setSzCs(sz(fontSize));
        return style;
    }

    private RPr rPr(Style style) {
        return get(style, Style::getRPr, style::setRPr, factory::createRPr);
    }

    public P styledP(String styleName, P p) {
        PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
        pStyle.setVal(styleName);
        pPr(p).setPStyle(pStyle);
        return p;
    }

    public Style paraStyle(Context context) {
        return fontSize(
                context.getFontSize(),
                createParaStyleBasedOn(
                        context.getParaStyleName(),
                        "Normal"));
    }

    public P zeroSpaceAfterP(P p) {
        PPrBase.Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setAfter(BigInteger.ZERO);
        pPr(p).setSpacing(spacing);
        return p;
    }

}
