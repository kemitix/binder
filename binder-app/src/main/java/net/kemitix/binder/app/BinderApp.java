package net.kemitix.binder.app;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final BinderConfig binderConfig;

    @Inject
    public BinderApp(
            BinderConfig binderConfig
    ) {
        this.binderConfig = binderConfig;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        log.info("Loading " + binderConfig.getScanDirectory());
        log.info("Binder - Done.");
    }

}
