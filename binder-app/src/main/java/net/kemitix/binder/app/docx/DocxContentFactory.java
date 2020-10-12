package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@Deprecated
@ApplicationScoped
public class DocxContentFactory {

    private final XHTMLImporter xhtmlImporter;

    @Inject
    public DocxContentFactory(XHTMLImporter xhtmlImporter) {
        this.xhtmlImporter = xhtmlImporter;
    }

    public DocxContent create(String name, String html) {
        log.info(String.format("Created Content: %s", name));
        try {
            return new DocxContent(xhtmlImporter.convert(html, "BASEURL"));
        } catch (Docx4JException e) {
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(name), e);
        }
    }
}
