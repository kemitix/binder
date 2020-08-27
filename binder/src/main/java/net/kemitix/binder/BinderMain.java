package net.kemitix.binder;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import net.kemitix.binder.app.BinderApp;

import javax.inject.Inject;

import static net.kemitix.binder.QuarkusBinderConfig.BINDER_DIRECTORY;

@QuarkusMain
public class BinderMain implements QuarkusApplication {

    private final BinderApp app;

    @Inject
    public BinderMain(BinderApp app) {
        this.app = app;
    }

    @Override
    public int run(String[] args) {
        if (args.length >= 1) {
            System.setProperty(BINDER_DIRECTORY, args[0]);
        }
        app.run(args);
        return 0;
    }
}
