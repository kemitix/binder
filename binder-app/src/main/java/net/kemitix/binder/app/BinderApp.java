package net.kemitix.binder.app;

import lombok.extern.java.Log;

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
        writers.stream()
                .forEach(ManuscriptWriter::write);
        log.info("Binder - Done.");
    }

}
