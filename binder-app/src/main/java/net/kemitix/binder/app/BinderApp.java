package net.kemitix.binder.app;

import lombok.extern.java.Log;
import net.kemitix.binder.app.epub.EpubFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final Manuscript manuscript;
    private final HtmlFactory htmlFactory;
    private final EpubFactory epubFactory;

    @Inject
    public BinderApp(
            Manuscript manuscript,
            HtmlFactory htmlFactory,
            EpubFactory epubFactory
    ) {
        this.manuscript = manuscript;
        this.htmlFactory = htmlFactory;
        this.epubFactory = epubFactory;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        manuscript.getContents().forEach(section ->
                log.info(String.format("%7s: %s", section.getType(), section.getName())));
        htmlFactory.createAll();
        epubFactory.create();
        log.info("Binder - Done.");
    }

}
