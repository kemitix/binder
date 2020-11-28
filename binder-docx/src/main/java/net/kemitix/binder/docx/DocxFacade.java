package net.kemitix.binder.docx;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTColumns;
import org.docx4j.wml.CTDocGrid;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
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
        implements DocxHeaderFootersFacadeMixIn,
        DocxFacadeParagraphMixIn,
        DocxFacadeFootnoteMixIn {

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

    @Override
    public AtomicInteger footnoteRef() {
        return myFootnoteRef;
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

    public Style paraStyle(Context context) {
        return fontSize(
                context.getFontSize(),
                createParaStyleBasedOn(
                        context.getParaStyleName(),
                        "Normal"));
    }

}
