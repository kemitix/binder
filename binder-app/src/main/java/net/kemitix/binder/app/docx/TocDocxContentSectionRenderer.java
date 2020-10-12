package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.SectionRenderer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

@ApplicationScoped
public class TocDocxContentSectionRenderer
        implements SectionRenderer<HtmlSection, DocxContent> {

    private final HtmlManuscript htmlManuscript;

    @Inject
    public TocDocxContentSectionRenderer(HtmlManuscript htmlManuscript) {
        this.htmlManuscript = htmlManuscript;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        //TODO: implement properly
        return new DocxContent(new ArrayList<>());
    }
}
