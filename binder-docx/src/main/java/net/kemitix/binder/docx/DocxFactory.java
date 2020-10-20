package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class DocxFactory {

    @Produces
    @ApplicationScoped
    public List<DocxContent> create(
            HtmlManuscript htmlManuscript,
            DocxSectionRenderer docxHtmlSectionRenderer
    ) {
        return htmlManuscript.sections()
                .filter(HtmlSection::isDocx)
                .map(docxHtmlSectionRenderer::render)
                .collect(Collectors.toList());
    }

}
