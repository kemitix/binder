package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigFileFilter {

    boolean test(String filename) {
        return filename.equals("binder.yaml");
    }

}
