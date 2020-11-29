package net.kemitix.binder.docx;

import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.UnitsOfMeasurement;
import org.docx4j.wml.CTColumns;
import org.docx4j.wml.CTDocGrid;
import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
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
            Context context,
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
            Context context,
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
        String name = context.getName();
        String title = context.getTitle();
        if (context.hasHeader()) {
            addEvenPageHeader(sectPr, name, textParagraphCentered(title));
            addDefaultPageHeader(sectPr, name,
                    textParagraphCentered("%s Issue %s"
                            .formatted(
                                    metadata().getTitle(),
                                    metadata().getIssue())));
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

    default P tocItem(String pageNumber, String title) {
        return tabDefinition(
                tabs(new CTTabStop[]{
                        tabLeft(0),
                        tabRight(576),
                        tabLeft(720)
                }),
                tabIndent(720, null, 720),
                p(new Object[]{
                        r(new Object[]{
                                tab(),
                                t(pageNumber),
                                tab(),
                                t(title)
                        })
                }));
    }

    default SectPr sectPr(List<P> sectionPs) {
        P last = last(sectionPs, P.class);
        PPr pPr = pPr(last);
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

    default <T> T last(List<T> items, Class<? extends T> tClass) {
        return items.get(items.size() - 1);
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

}
