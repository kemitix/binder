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
            Manuscript manuscript) {
        this.manuscript = manuscript;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        manuscript.getContents().forEach(section ->
                log.info(String.format("%7s: %s", section.getType(), section.getName())));
        System.out.println("contents = " + manuscript.getContents());
        log.info("Binder - Done.");
    }

}
