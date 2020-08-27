package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ManuscriptLoader {

    @Produces
    Manuscript manuscript() {
        return new Manuscript();
    }

}
