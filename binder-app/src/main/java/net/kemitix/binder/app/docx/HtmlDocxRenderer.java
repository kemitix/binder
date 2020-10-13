package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class HtmlDocxRenderer
        implements DocxRenderer {

    XHTMLImporterImpl xhtmlImporter() throws InvalidFormatException {
        WordprocessingMLPackage aPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(aPackage);
        xhtmlImporter.setHyperlinkStyle("Hyperlink");
        return xhtmlImporter;
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
                    xhtmlImporter().convert(htmlSection.getHtml(),
                            "BASEURL"));
        } catch (Docx4JException e) {
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(htmlSection.getName()), e);
        }
    }
}
