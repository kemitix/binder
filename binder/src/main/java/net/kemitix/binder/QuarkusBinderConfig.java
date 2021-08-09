package net.kemitix.binder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.Objects;

@Log
@Setter
@Getter
@ApplicationScoped
public class QuarkusBinderConfig
        implements BinderConfig {

    private File scanDirectory =
            new File(binderDirectory());

    private String binderDirectory() {
        return environment("BINDER_DIR")
                .replaceFirst("^~", environment("user.home"));
    }

    private String environment(String s) {
        return Objects.requireNonNull(
                System.getenv(s),
                "Undefined property: " + s);
    }

    @PostConstruct
    void init() {
        log.info(String.format("BINDER_DIR: %s", scanDirectory));
    }

}
