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
        DocxFacadeStyleMixIn,
        DocxFacadeFootnoteMixIn,
        DocxFacadeTabMixIn {

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

}
