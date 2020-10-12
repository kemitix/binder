package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class DocxFactory {

    private final BinderConfig binderConfig;
    private final HtmlManuscript htmlManuscript;
    private final DocxHtmlSectionRenderer docxHtmlSectionRenderer;

    @Inject
    public DocxFactory(
            BinderConfig binderConfig,
            HtmlManuscript htmlManuscript,
            DocxHtmlSectionRenderer docxHtmlSectionRenderer
    ) {
        this.binderConfig = binderConfig;
        this.htmlManuscript = htmlManuscript;
        this.docxHtmlSectionRenderer = docxHtmlSectionRenderer;
    }

    @Produces
    @ApplicationScoped
    public List<DocxContent> create() {
        return htmlManuscript.sections()
                .filter(HtmlSection::isDocx)
                .map(docxHtmlSectionRenderer::renderContent)
                .collect(Collectors.toList());
    }

}
