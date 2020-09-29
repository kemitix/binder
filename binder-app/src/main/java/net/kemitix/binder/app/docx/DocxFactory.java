package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.Metadata;
import net.kemitix.binder.app.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Log
@ApplicationScoped
public class DocxFactory {

    private final BinderConfig binderConfig;
    private final HtmlManuscript htmlManuscript;
    private final DocxContentFactory docxContextFactory;

    @Inject
    public DocxFactory(
            BinderConfig binderConfig,
            HtmlManuscript htmlManuscript,
            DocxContentFactory docxContextFactory
    ) {
        this.binderConfig = binderConfig;
        this.htmlManuscript = htmlManuscript;
        this.docxContextFactory = docxContextFactory;
    }

    @Produces
    @ApplicationScoped
    public DocxBook create() {
        Metadata metadata = htmlManuscript.getMetadata();
        DocxBook docx = createDocxBook(metadata);
        htmlManuscript.getHtmlSections().entrySet().stream()
                .filter(includeInDocx(htmlManuscript.getContents()))
                .map(e -> docxContextFactory.create(e.getKey(), e.getValue()))
                .forEach(docx::addContent);
        return docx;
    }

    private Predicate<Map.Entry<String, String>> includeInDocx(List<Section> contents) {
        return entry -> contents.stream()
                .filter(Section::isDocx)
                .map(Section::getName)
                .anyMatch(name -> name.equals(entry.getKey()));
    }

    private DocxBook createDocxBook(Metadata metadata) {
        String language = metadata.getLanguage();
        log.info("Language: " + language);
        String id = metadata.getId();
        log.info("Id: " + id);
        String title = metadata.getTitle();
        log.info("Title: " + title);
        String editor = metadata.getEditor();
        log.info("Editor: " + editor);
        return new DocxBook(language, id, title, editor);
    }
}
