package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.Manuscript;
import net.kemitix.binder.app.ManuscriptMetadata;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class EpubFactory {

    private final BinderConfig binderConfig;
    private final Manuscript manuscript;
    private final EpubContentFactory epubContentFactory;

    @Inject
    public EpubFactory(
            BinderConfig binderConfig,
            Manuscript manuscript,
            EpubContentFactory epubContentFactory
    ) {
        this.binderConfig = binderConfig;
        this.manuscript = manuscript;
        this.epubContentFactory = epubContentFactory;
    }

    public void create() {
        ManuscriptMetadata metadata = manuscript.getMetadata();
        EpubBook epub = createEpub(metadata);
        //TODO epub.addCoverImage(coverImageBytes, mediaType, href);
        epub.addTextContent("", "", "");
        manuscript.getContents().stream()
                .map(epubContentFactory::create)
                .forEach(epub::addContent);
        String epubFile = binderConfig.getEpubFile().getAbsolutePath();
        try {
            epub.writeToFile(epubFile);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error creating epub file %s: %s",
                    epubFile, e.getMessage()), e);
        }
    }

    @NotNull
    private EpubBook createEpub(ManuscriptMetadata metadata) {
        String language = metadata.getLanguage();
        log.info("Language: " + language);
        String id = metadata.getId();
        log.info("Id: " + id);
        String title = metadata.getTitle();
        log.info("Title: " + title);
        String editor = metadata.getEditor();
        log.info("Editor: " + editor);
        return new EpubBook(language, id, title, editor);
    }
}
