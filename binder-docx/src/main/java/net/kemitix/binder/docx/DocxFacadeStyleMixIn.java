package net.kemitix.binder.docx;

import lombok.SneakyThrows;
import net.kemitix.binder.markdown.Context;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Style;

import java.math.BigInteger;
import java.util.List;

public interface DocxFacadeStyleMixIn
        extends DocxFacadeMixIn {
    
    @SneakyThrows
    default void addStyle(Style style) {
        var part = mainDocumentPart()
                .getStyleDefinitionsPart(true);
        List<Style> styles = part.getContents().getStyle();
        styles.add(style);
    }

    default HpsMeasure sz(float fontSize) {
        HpsMeasure hpsMeasure = factory().createHpsMeasure();
        hpsMeasure.setVal(BigInteger.valueOf((long) (fontSize * 2)));
        return hpsMeasure;
    }

    default Style createParaStyleBasedOn(String name, String basedOn) {
        Style style = named(name, basedOn(basedOn, createStyle(name)));
        style.setType("paragraph");
        return style;
    }

    default Style named(String name, Style style) {
        Style.Name styleName = factory().createStyleName();
        styleName.setVal(name);
        style.setName(styleName);
        return style;
    }

    default Style basedOn(String name, Style style) {
        Style.BasedOn basedOn = factory().createStyleBasedOn();
        basedOn.setVal(name);
        style.setBasedOn(basedOn);
        return style;
    }

    default Style createCharStyleBasedOn(String name, String basedOn) {
        Style style = named(name, basedOn(basedOn, createStyle(name)));
        style.setType("character");
        return style;
    }

    default Style createStyle(String name) {
        Style style = factory().createStyle();
        style.setStyleId(name);
        return style;
    }

    default Style fontSize(float fontSize, Style style) {
        RPr rPr = rPr(style);
        rPr.setSz(sz(fontSize));
        rPr.setSzCs(sz(fontSize));
        return style;
    }

    default RPr rPr(Style style) {
        return get(style, Style::getRPr, style::setRPr, factory()::createRPr);
    }

    default Style paraStyle(Context context) {
        return fontSize(
                context.getFontSize(),
                createParaStyleBasedOn(
                        context.getParaStyleName(),
                        "Normal"));
    }

}
