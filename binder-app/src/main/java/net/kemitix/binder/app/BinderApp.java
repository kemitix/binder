package net.kemitix.binder.app;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final Manuscript manuscript;
    private final HtmlFactory htmlFactory;

    @Inject
    public BinderApp(
            Manuscript manuscript,
            HtmlFactory htmlFactory
    ) {
        this.manuscript = manuscript;
        this.htmlFactory = htmlFactory;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        manuscript.getContents().forEach(section ->
                log.info(String.format("%7s: %s", section.getType(), section.getName())));
        htmlFactory.createAll();
        log.info("Binder - Done.");
    }

}
