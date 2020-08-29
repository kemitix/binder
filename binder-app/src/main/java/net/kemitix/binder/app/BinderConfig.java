package net.kemitix.binder.app;

import java.io.File;

public interface BinderConfig {
    File getScanDirectory();

    default File getFile(String name, String extension) {
        return getScanDirectory().toPath()
                .resolve(String.format("%s.%s", name, extension))
                .toFile();
    }
}
