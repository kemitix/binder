package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.Manuscript;
import net.kemitix.binder.app.Metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class DocxFactory {

    private final BinderConfig binderConfig;
    private final Manuscript manuscript;
    private final DocxContentFactory docxContextFactory;

    @Inject
    public DocxFactory(
            BinderConfig binderConfig,
            Manuscript manuscript,
            DocxContentFactory docxContextFactory
    ) {
        this.binderConfig = binderConfig;
        this.manuscript = manuscript;
        this.docxContextFactory = docxContextFactory;
    }

    public DocxBook create() {
        Metadata metadata = manuscript.getMetadata();
        DocxBook docx = createDocxBook(metadata);
        manuscript.getContents().stream()
                .map(docxContextFactory::create)
                .forEach(docx::addContent);
        return docx;
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
