package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.SectionRenderer;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HtmlDocxContentSectionRenderer
        implements SectionRenderer<HtmlSection, DocxContent> {

    private final XHTMLImporter xhtmlImporter;

    @Inject
    public HtmlDocxContentSectionRenderer(XHTMLImporter xhtmlImporter) {
        this.xhtmlImporter = xhtmlImporter;
    }

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        try {
            return new DocxContent(
                    xhtmlImporter.convert(htmlSection.getHtml(),
                            "BASEURL"));
        } catch (Docx4JException e) {
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(htmlSection.getName()), e);
        }
    }
}
