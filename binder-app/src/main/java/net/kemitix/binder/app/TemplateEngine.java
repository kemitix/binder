package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TemplateEngine {

    public String resolve(String body, Section section) {
        return body;
    }

}
