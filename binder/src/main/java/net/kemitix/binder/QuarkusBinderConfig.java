package net.kemitix.binder;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;

import javax.enterprise.context.ApplicationScoped;

@Log
@Setter
@Getter
@ApplicationScoped
public class QuarkusBinderConfig
        implements BinderConfig {
}
