package net.kemitix.binder.docx;

import org.docx4j.wml.CTBorder;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tabs;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.docx4j.wml.PPrBase.*;

public interface DocxFacadeParagraphMixIn
        extends DocxFacadeRunMixIn {

    default P textParagraph(String text) {
        return p(r(t(text)));
    }

    default P textParagraphCentered(String text) {
        return alignCenter(p(r(t(text))));
    }

    default P p() {
        return p(new Object[]{});
    }

    default P zeroSpaceAfterP(P p) {
        Spacing spacing = factory().createPPrBaseSpacing();
        spacing.setAfter(BigInteger.ZERO);
        pPr(p).setSpacing(spacing);
        return p;
    }

    default P p(Object o) {
        return p(new Object[]{o});
    }

    default P p(Object[] o) {
        P p = factory().createP();
        p.getContent().addAll(Arrays.asList(o));
        return p;
    }

    default P align(JcEnumeration alignment, P p) {
        pPr(p).setJc(jc(alignment));
        return p;
    }

    default P alignFull(P p) {
        return align(JcEnumeration.BOTH, p);
    }

    default P alignLeft(P p) {
        return align(alignmentLeft(), p);
    }

    default P alignRight(P p) {
        return align(alignmentRight(), p);
    }

    default P alignCenter(P p) {
        return align(alignmentCenter(), p);
    }

    default JcEnumeration alignmentBoth() {
        return JcEnumeration.BOTH;
    }

    default JcEnumeration alignmentLeft() {
        return JcEnumeration.LEFT;
    }

    default JcEnumeration alignmentRight() {
        return JcEnumeration.RIGHT;
    }

    default JcEnumeration alignmentCenter() {
        return JcEnumeration.CENTER;
    }

    default P drawings(Drawing[] drawings) {
        R r = r();
        List<Object> content = r.getContent();
        Collections.addAll(content, drawings);
        return alignCenter(p(r));
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
    default P heading(int level, String text) {
        R r = r(t(text));
        P p = p(r);
        PPr pPr = pPr(p);
        ParaRPr paraRPr = paraRPr(pPr);
        RPr rPr = rPr(r);

        pPr.setPStyle(pStyle("Normal"));

        HpsMeasure sz = factory().createHpsMeasure();
        sz.setVal(BigInteger.valueOf(szForLevel(level)));

        rPr.setSz(sz);
        rPr.setSzCs(sz);

        paraRPr.setSz(sz);
        paraRPr.setSzCs(sz);

        return p;
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
    default P bulletItem(String text) {
        P p = p(r(t(text)));

        PPr pPr = pPr(p);
        pPr.setPStyle(pStyle("Normal"));

        NumPr.NumId numId = factory().createPPrBaseNumPrNumId();
        numId.setVal(BigInteger.TWO);
        NumPr numPr = factory().createPPrBaseNumPr();
        NumPr.Ilvl ilvl = factory().createPPrBaseNumPrIlvl();
        ilvl.setVal(BigInteger.ZERO);
        numPr.setIlvl(ilvl);
        numPr.setNumId(numId);
        pPr.setNumPr(numPr);

        return p;
    }

    default Object blockquote(P p) {
        PBdr pBdr = pBdr(p);
        // set left border
        CTBorder left = left(pBdr);
        left.setVal(STBorder.SINGLE);
        left.setSz(BigInteger.TWO);
        left.setSpace(BigInteger.TEN);
        left.setColor("000000");
        Ind ind = ind(p);
        // set left and hang
        BigInteger margin = BigInteger.valueOf(400);
        ind.setLeft(margin);
        ind.setRight(margin);
        return p;
    }

    private CTBorder left(PBdr pBdr) {
        return get(pBdr, PBdr::getLeft, pBdr::setLeft, this::ctBorder);
    }

    private CTBorder ctBorder() {
        return factory().createCTBorder();
    }

    //p/pPr/keepNext[val="true"]
    default P keepWithNext(P p) {
        pPr(p).setKeepNext(defaultTrue());
        return p;
    }

    default P keepTogether(P p) {
        pPr(p).setKeepLines(defaultTrue());
        return p;
    }

    default P styleP(String styleName, P p) {
        pPr(p).setPStyle(pStyle(styleName));
        return p;
    }

    default P styledP(String styleName, P p) {
        PStyle pStyle = factory().createPPrBasePStyle();
        pStyle.setVal(styleName);
        pPr(p).setPStyle(pStyle);
        return p;
    }

    default P tabDefinition(Tabs tabs, Ind tabIndent, P p) {
        PPr pPr = pPr(p);
        pPr.setTabs(tabs);
        pPr.setInd(tabIndent);
        return p;
    }

    default PPr pPr() {
        return factory().createPPr();
    }

    default PPr pPr(P p) {
        return get(p, P::getPPr, p::setPPr, this::pPr);
    }

    default ParaRPr paraRPr(PPr pPr) {
        return get(pPr, PPr::getRPr, pPr::setRPr, factory()::createParaRPr);
    }

    default Jc jc(JcEnumeration value) {
        Jc jc = factory().createJc();
        jc.setVal(value);
        return jc;
    }

    default PStyle pStyle(String val) {
        var pStyle = factory().createPPrBasePStyle();
        pStyle.setVal(val);
        return pStyle;
    }

    default PBdr pBdr(P p) {
        PPr pPr = pPr(p);
        return get(pPr, PPr::getPBdr, pPr::setPBdr, this::pBdr);
    }

    default PBdr pBdr() {
        return factory().createPPrBasePBdr();
    }

    default Ind ind(P p) {
        PPr pPr = pPr(p);
        return get(pPr, PPr::getInd, pPr::setInd, this::ind);
    }

    default Ind ind() {
        return factory().createPPrBaseInd();
    }

    // h1 = 24pt
    // h2 = 18pt
    // h3 = 12pt
    // lvl => 30 - (6 * lvl)
    default long szForLevel(int level) {
        int point = 30 - (6 * level);
        return point * 2;
    }

}
