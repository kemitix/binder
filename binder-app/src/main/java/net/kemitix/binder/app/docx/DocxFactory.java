package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
