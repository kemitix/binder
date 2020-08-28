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
        manuscript.getPreludes().forEach(prelude -> log.info("Prelude: " + prelude.getName()));
        manuscript.getContents().forEach(section -> log.info("Content: " + section.getName()));
        manuscript.getCodas().forEach(coda -> log.info("Coda   : " + coda.getName()));
        log.info("Binder - Done.");
    }

}
