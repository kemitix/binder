package net.kemitix.binder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@Log
@Setter
@Getter
@ApplicationScoped
public class QuarkusBinderConfig
        implements BinderConfig {

    private String scanDirectory = System.getenv("BINDER_DIRECTORY");

    @PostConstruct
    void init() {
        log.info(String.format("BINDER_DIRECTORY: %s", scanDirectory));
    }

}
