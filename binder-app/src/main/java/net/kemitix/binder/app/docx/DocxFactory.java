package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class DocxFactory {

    private final BinderConfig binderConfig;
    private final HtmlManuscript htmlManuscript;
    private final DocxContentFactory docxContentFactory;

    @Inject
    public DocxFactory(
            BinderConfig binderConfig,
            HtmlManuscript htmlManuscript,
            DocxContentFactory docxContentFactory
    ) {
        this.binderConfig = binderConfig;
        this.htmlManuscript = htmlManuscript;
        this.docxContentFactory = docxContentFactory;
    }

    @Produces
    @ApplicationScoped
    public List<DocxContent> create() {
        return htmlManuscript.getHtmlSections().entrySet().stream()
                .filter(includeInDocx(htmlManuscript.getContents()))
                .map(e -> docxContentFactory.create(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private Predicate<Map.Entry<String, String>> includeInDocx(List<Section> contents) {
        return entry -> contents.stream()
                .filter(Section::isDocx)
                .map(Section::getName)
                .anyMatch(name -> name.equals(entry.getKey()));
    }

}
