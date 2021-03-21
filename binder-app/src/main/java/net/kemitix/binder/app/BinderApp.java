package net.kemitix.binder.app;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.mon.result.Result;

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
        Result.applyOver(writers.stream(), ManuscriptWriter::write)
                .onError(e -> log.severe(e.getMessage()));
        log.info("Binder - Done.");
    }

}
