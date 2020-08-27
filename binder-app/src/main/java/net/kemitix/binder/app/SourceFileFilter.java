package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class SourceFileFilter {

    @Inject ConfigFileFilter configFileFilter;

    boolean test(String filename) {
        return filename.endsWith(".md") ||
                configFileFilter.test(filename);
    }

}
