package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SourceFileFilter {

    boolean test(String filename) {
        return filename.equals("binder.json");
    }

}
