package net.kemitix.binder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@Log
@Setter
@Getter
@ApplicationScoped
public class QuarkusBinderConfig
        implements BinderConfig {

    private static final String BINDER_DIRECTORY = "BINDER_DIRECTORY";

    private String scanDirectory = Objects.requireNonNull(
            System.getenv(BINDER_DIRECTORY), BINDER_DIRECTORY);

    @PostConstruct
    void init() {
        log.info(String.format("BINDER_DIRECTORY: %s", scanDirectory));
    }

}
