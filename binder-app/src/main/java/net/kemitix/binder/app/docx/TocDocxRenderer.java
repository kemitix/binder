package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Log
@ApplicationScoped
public class TocDocxRenderer
        implements DocxRenderer {

    private final HtmlManuscript htmlManuscript;

    @Inject
    public TocDocxRenderer(HtmlManuscript htmlManuscript) {
        this.htmlManuscript = htmlManuscript;
    }

    @Override
    public boolean canHandle(String type) {
        return "toc".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("TOC: %s".formatted(htmlSection.getName()));
        var document = createDocument();
        document.addStyledParagraphOfText("Title", "Table of Contents");
        document.addParagraphOfText("blah blah");
        List<Object> content = document.getContent();
        log.info("TOC items: %d".formatted(content.size()));
        return new DocxContent(content);
    }

    private MainDocumentPart createDocument() {
        try {
            return WordprocessingMLPackage.createPackage()
                    .getMainDocumentPart();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
