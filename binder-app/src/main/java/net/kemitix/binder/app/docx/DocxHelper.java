package net.kemitix.binder.app.docx;

import org.docx4j.wml.Br;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DocxHelper {

    private final ObjectFactory objectFactory;

    @Inject
    public DocxHelper(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public P breakToOddPage() {
        return p(ppr(sectPr(sectPrType("oddPage"))));
    }

    @NotNull
    private PPr ppr(SectPr sectPr) {
        PPr pPr = objectFactory.createPPr();
        pPr.setSectPr(sectPr);
        return pPr;
    }

    @NotNull
    private SectPr sectPr(SectPr.Type type) {
        SectPr sectPr = objectFactory.createSectPr();
        sectPr.setType(type);
        return sectPr;
    }

    private SectPr.Type sectPrType(String value) {
        SectPr.Type type = new SectPr.Type();
        type.setVal(value);
        return type;
    }

    public P textParagraph(String text) {
        return p(r(text(text)));
    }

    private P p(Object o) {
        P p = objectFactory.createP();
        p.getContent().add(o);
        return p;
    }

    private R r(Object o) {
        R r = objectFactory.createR();
        r.getContent().add(o);
        return r;
    }

    private Text text(String value) {
        Text text = objectFactory.createText();
        text.setValue(value);
        return text;
    }

}
