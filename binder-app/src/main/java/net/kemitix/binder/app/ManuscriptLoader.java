package net.kemitix.binder.app;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@Log
@ApplicationScoped
public class ManuscriptLoader {

    @Produces
    Manuscript manuscript(BinderConfig binderConfig) {
        log.info("Loading Manuscript from " + binderConfig.getScanDirectory());
        return new Manuscript();
    }

}
