package net.kemitix.binder.app;

import lombok.extern.java.Log;
import net.kemitix.binder.app.epub.EpubFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final HtmlFactory htmlFactory;
    private final EpubFactory epubFactory;
    private final EpubWriter epubWriter;

    @Inject
    public BinderApp(
            HtmlFactory htmlFactory,
            EpubFactory epubFactory,
            EpubWriter epubWriter
    ) {
        this.htmlFactory = htmlFactory;
        this.epubFactory = epubFactory;
        this.epubWriter = epubWriter;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        htmlFactory.createAll();
        epubWriter.write(epubFactory.create());
        log.info("Binder - Done.");
    }

}
