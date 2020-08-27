package net.kemitix.binder.app;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final Manuscript manuscript;

    @Inject
    public BinderApp(
            Manuscript manuscript
    ) {
        this.manuscript = manuscript;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        manuscript.getPrelude().forEach(prelude -> log.info("Prelude: " + prelude.getName()));
        manuscript.getContents().forEach(section -> log.info("Prelude: " + section.getName()));
        manuscript.getCoda().forEach(coda -> log.info("Prelude: " + coda.getName()));
        log.info("Binder - Done.");
    }

}
