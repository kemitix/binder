package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class HtmlDocxRenderer
        implements DocxRenderer {

    private final XHTMLImporter xhtmlImporter;

    @Inject
    public HtmlDocxRenderer(XHTMLImporter xhtmlImporter) {
        this.xhtmlImporter = xhtmlImporter;
    }

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("HTML: %s".formatted(htmlSection.getName()));
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
