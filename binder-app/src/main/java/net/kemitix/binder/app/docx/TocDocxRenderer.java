package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.AggregateRenderer;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class TocDocxRenderer
        implements DocxRenderer,
        AggregateRenderer<DocxTocItemRenderer, HtmlSection, Object> {

    private final HtmlManuscript htmlManuscript;
    private final ObjectFactory factory;
    private final Instance<DocxTocItemRenderer> tocItemRenderers;

    @Inject
    public TocDocxRenderer(
            HtmlManuscript htmlManuscript,
            ObjectFactory factory,
            Instance<DocxTocItemRenderer> tocItemRenderers
    ) {
        this.htmlManuscript = htmlManuscript;
        this.factory = factory;
        this.tocItemRenderers = tocItemRenderers;
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
        htmlManuscript.sections()
                .filter(HtmlSection::isDocx)
                .filter(HtmlSection::isToc)
                .forEach(section -> content.add(
                        findRenderer(section.getType(), tocItemRenderers)
                                .render(section)));
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
