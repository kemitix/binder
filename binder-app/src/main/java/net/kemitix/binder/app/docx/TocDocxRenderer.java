package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class TocDocxRenderer
        implements DocxRenderer {

    private final HtmlManuscript htmlManuscript;
    private final ObjectFactory factory;

    @Inject
    public TocDocxRenderer(HtmlManuscript htmlManuscript, ObjectFactory factory) {
        this.htmlManuscript = htmlManuscript;
        this.factory = factory;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("TOC: %s".formatted(htmlSection.getName()));
        List<Object> content = new ArrayList<>();
        //TODO insert page break
        content.add(paragraph("Table of Contents"));
        content.add(paragraph("blah blah"));
        return new DocxContent(content);
    }

    private P paragraph(String s) {
        P p1 = factory.createP();
        R r1 = factory.createR();
        Text title = factory.createText();
        title.setValue(s);
        r1.getContent().add(title);
        p1.getContent().add(r1);
        return p1;
    }

}
