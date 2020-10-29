package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlSection;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log
@ApplicationScoped
public class HtmlDocxRenderer
        implements DocxRenderer {

    private final DocxFacade docx;
    private final DocxImageFacade docxImage;

    @Inject
    public HtmlDocxRenderer(
            DocxFacade docx,
            DocxImageFacade docxImage
    ) {
        this.docx = docx;
        this.docxImage = docxImage;
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
            List<Object> contents = new ArrayList<>();
            if ("story".equals(htmlSection.getType())) {
                contents.addAll(docx.leaders());
            }
            addTitle(htmlSection, contents);
            if ("story".equals(htmlSection.getType())) {
                contents.add(docx.textParagraphCentered(htmlSection.getAuthor()));
                contents.addAll(docx.leaders());
            }
            List<Object> objects = xhtmlImporter().convert(htmlDocument(htmlSection), "BASEURL");
            contents.addAll(objects);
            contents.add(docx.breakToOddPage());
            return new DocxContent(contents);
        } catch (Docx4JException e) {
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(htmlSection.getName()), e);
        }
    }

    private String htmlDocument(HtmlSection htmlSection) {
        return """
                    <html><head><title>%s</title></head>
                    <body>
                    %s
                    </body>
                    </html>
                    """.formatted(
                htmlSection.getTitle(),
                htmlSection.getHtml());
    }

    private void addTitle(HtmlSection htmlSection, List<Object> contents) {
        String title = getTitle(htmlSection);
        if (title.length() > 0) {
            contents.add(docx.textParagraph(""));
            contents.add(
                    docx.drawings(
                            docxImage.textImages(
                                    title,
                                    FontSize.of(240))));
            contents.add(docx.textParagraph(""));
        }
    }

    private String getTitle(HtmlSection htmlSection) {
        return Objects.requireNonNullElse(htmlSection.getTitle(), "");
    }
}
