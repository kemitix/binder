package net.kemitix.binder.app;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.UnhandledMarkdownException;
import net.kemitix.binder.spi.BinderException;
import net.kemitix.binder.spi.ManuscriptWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final Instance<ManuscriptWriter> writers;

    @Inject
    public BinderApp(
            Instance<ManuscriptWriter> writers
    ) {
        this.writers = writers;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        try {
            writers.stream()
                    .forEach(ManuscriptWriter::write);
        } catch (UnhandledMarkdownException e) {
            log.severe(e.getMessage());
        } catch (BinderException e) {
            log.severe(e.getMessage());
        }
        log.info("Binder - Done.");
    }

}
