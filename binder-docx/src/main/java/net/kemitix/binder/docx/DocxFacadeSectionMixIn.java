package net.kemitix.binder.docx;

import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.wml.CTColumns;
import org.docx4j.wml.CTDocGrid;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.SectPr;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public interface DocxFacadeSectionMixIn
        extends DocxFacadeHeaderMixIn,
        DocxFacadeFooterMixIn,
        DocxFacadeTabMixIn {

    Metadata metadata();

    default List<P> finaliseTitlePage(
            Context<DocxRenderHolder> context,
            List<Object> pageContent
    ) {
        // force into a list of P - we have an invalid section if this results in ClassCastException
        List<P> sectionPs = pageContent.stream()
                .map(P.class::cast)
                .collect(Collectors.toList());
        SectPr sectPrContent = sectPr(sectionPs);
        SectPr sectPrType =
                context.startOnOddPage()
                        ? sectPrType("oddPage", sectPrContent)
                        : sectPrContent;
        SectPr sectPr = sizePage(sectPrType);
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

    default List<P> finaliseSection(
            Context<DocxRenderHolder> context,
            List<Object> pageContent
    ) {
        // force into a list of P - we have an invalid section if this results in ClassCastException
        List<P> sectionPs = pageContent.stream()
                .map(P.class::cast)
                .collect(Collectors.toList());
        SectPr sectPr =
                sizePage(
                        sectPrType("oddPage",
                                sectPr(sectionPs)));
        Section.Name name = context.getName();
        String title = context.getTitle();
        if (context.hasHeader()) {
            addEvenPageHeader(sectPr, name, textParagraphCentered(title));
            String oddPageHeadingText = metadata().getTitle();
            addDefaultPageHeader(sectPr, name,
                    textParagraphCentered(oddPageHeadingText));
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

    default P tocItem(String pageNumber, String title, String author) {
        return tabDefinition(
                tabs(new CTTabStop[]{
                        tabLeft(0),
                        tabRight(576),
                        tabLeft(720),
                        tabLeft(900)
                }),
                tabIndent(720, null, 720),
                p(new Object[]{
                        r(new Object[]{
                                tab(),
                                t(pageNumber),
                                tab(),
                                t(title)
                        }),
                        r(new Object[]{
                                br(),
                                tab(),
                                tab()
                        }),
                        italic(r(t(author)))
                }));
    }

    default SectPr sectPr(List<P> sectionPs) {
        P p = factory().createP();
        sectionPs.add(p);
        PPr pPr = pPr(p);
        return get(pPr, PPr::getSectPr, pPr::setSectPr, factory()::createSectPr);
    }

    default SectPr sizePage(SectPr sectPr) {
        sectPr.setPgSz(pgSz());
        sectPr.setPgMar(pgMar());
        return sectPr;
    }

    default SectPr formatSection(SectPr sectPr) {
        CTColumns ctColumns = factory().createCTColumns();
        sectPr.setCols(ctColumns);
        BigInteger topBottom = BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata().getPaperbackMarginTopBottom()));
        ctColumns.setSpace(topBottom);

        CTDocGrid ctDocGrid = factory().createCTDocGrid();
        sectPr.setDocGrid(ctDocGrid);
        ctDocGrid.setLinePitch(topBottom);

        return sectPr;
    }

    default SectPr.PgMar pgMar() {
        SectPr.PgMar pgMar = factory().createSectPrPgMar();
        BigInteger sides = BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata().getPaperbackMarginSides()));
        BigInteger topBottom = BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata().getPaperbackMarginTopBottom()));
        pgMar.setTop(topBottom);
        pgMar.setBottom(topBottom);
        pgMar.setLeft(sides);
        pgMar.setRight(sides);
        pgMar.setHeader(topBottom);
        pgMar.setFooter(topBottom);
        pgMar.setGutter(BigInteger.ZERO);
        return pgMar;
    }

    default SectPr.PgSz pgSz() {
        SectPr.PgSz pgSz = factory().createSectPrPgSz();
        pgSz.setH(BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata().getPaperbackPageHeightInches())));
        pgSz.setW(BigInteger.valueOf(
                UnitsOfMeasurement.inchToTwip(
                        metadata().getPaperbackPageWidthInches())));
        return pgSz;
    }

    default SectPr sectPrType(String value, SectPr sectPr) {
        get(sectPr, SectPr::getType, sectPr::setType, SectPr.Type::new)
                .setVal(value);
        return sectPr;
    }

    default P pageBreak() {
        //      <w:r>
        //        <w:br w:type="page"/>
        //      </w:r>
        return p(r(br(STBrType.PAGE)));
    }
}
