package net.kemitix.binder.app;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.app.epub.EpubFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final BinderConfig binderConfig;
    private final Manuscript manuscript;
    private final HtmlFactory htmlFactory;
    private final EpubFactory epubFactory;

    @Inject
    public BinderApp(
            BinderConfig binderConfig,
            Manuscript manuscript,
            HtmlFactory htmlFactory,
            EpubFactory epubFactory
    ) {
        this.binderConfig = binderConfig;
        this.manuscript = manuscript;
        this.htmlFactory = htmlFactory;
        this.epubFactory = epubFactory;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        manuscript.getContents().forEach(section ->
                log.info(String.format("%7s: %s", section.getType(), section.getName())));
        htmlFactory.createAll();
        write(epubFactory.create());
        log.info("Binder - Done.");
    }

    private void write(EpubBook epubBook) {
        String epubFile = binderConfig.getEpubFile().getAbsolutePath();
        try {
            epubBook.writeToFile(epubFile);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error creating epub file %s: %s",
                    epubFile, e.getMessage()), e);
        }
    }

}
