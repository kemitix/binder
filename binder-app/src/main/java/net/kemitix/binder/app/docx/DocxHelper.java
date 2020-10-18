package net.kemitix.binder.app.docx;

import org.docx4j.wml.Br;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.SectPr;
import org.docx4j.wml.Text;

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
        SectPr.Type type = new SectPr.Type();
        type.setVal("oddPage");

        SectPr sectPr = objectFactory.createSectPr();
        sectPr.setType(type);

        Br br = objectFactory.createBr();
        br.setType(STBrType.PAGE);

        PPr pPr = objectFactory.createPPr();
        pPr.setSectPr(sectPr);

        P p = objectFactory.createP();
        p.getContent().add(pPr);

        return p;
    }

//    public P pageBreak() {
//        Br br = objectFactory.createBr();
//        br.setType(STBrType.PAGE);
//        R r = objectFactory.createR();
//        r.getContent().add(br);
//        P p = objectFactory.createP();
//        p.getContent().add(r);
//        return p;
//    }

    public P textParagraph(String text) {
        Text title = objectFactory.createText();
        title.setValue(text);
        R r = objectFactory.createR();
        r.getContent().add(title);
        P p = objectFactory.createP();
        p.getContent().add(r);
        return p;
    }

}
