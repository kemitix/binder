package net.kemitix.binder.docx;

import org.docx4j.wml.Br;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Text;

import javax.xml.bind.JAXBElement;
import java.util.Arrays;

public interface DocxFacadeRunMixIn
        extends DocxFacadeMixIn {

    default R[] pageNumberPlaceholder() {
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
        FldChar begin = factory().createFldChar();
        begin.setFldCharType(STFldCharType.BEGIN);

        return new R[]{
                r(fldChar(STFldCharType.BEGIN)),
                r(instrText(" PAGE ")),
                r(fldChar(STFldCharType.SEPARATE)),
                r(t("2")),
                r(fldChar(STFldCharType.END))
        };
    }

    default FldChar fldChar(STFldCharType type) {
        FldChar fldChar = factory().createFldChar();
        fldChar.setFldCharType(type);
        return fldChar;
    }

    default JAXBElement<Text> instrText(String text) {
        return factory().createRInstrText(t(text));
    }

    default RPr rPr(R r) {
        return get(r, R::getRPr, r::setRPr, factory()::createRPr);
    }

    default R r() {
        return r(new Object[]{});
    }

    default R r(Object o) {
        return r(new Object[]{o});
    }

    default R r(Object[] o) {
        R r = factory().createR();
        r.getContent().addAll(Arrays.asList(o));
        return r;
    }

    default Text t(String value) {
        Text text = factory().createText();
        text.setValue(value);
        text.setSpace("preserve");// contains significant whitespace
        return text;
    }

    default Br br() {
        return factory().createBr();
    }

    default R italic(R r) {
        rPr(r).setI(defaultTrue());
        return r;
    }

    default R bold(R r) {
        rPr(r).setB(defaultTrue());
        return r;
    }

    default R strikethrough(R r) {
        rPr(r).setStrike(defaultTrue());
        return r;
    }

    default RStyle rStyle(String val) {
        var rStyle = factory().createRStyle();
        rStyle.setVal(val);
        return rStyle;
    }

    default Br textLineBreak() {
        return factory().createBr();
    }
}
