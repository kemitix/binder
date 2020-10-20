package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.HtmlSection;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class HtmlDocxRenderer
        implements DocxRenderer {

    private final DocxHelper docxHelper;

    @Inject
    public HtmlDocxRenderer(DocxHelper docxHelper) {
        this.docxHelper = docxHelper;
    }

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
            List<Object> contents = new ArrayList<>(
                    xhtmlImporter().convert(htmlSection.getHtml(),
                            "BASEURL"));
            contents.add(docxHelper.breakToOddPage());
            return new DocxContent(contents);
        } catch (Docx4JException e) {
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(htmlSection.getName()), e);
        }
    }
}
