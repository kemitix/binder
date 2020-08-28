package net.kemitix.binder.app;

import java.io.File;

public interface BinderConfig {
    File getScanDirectory();

    File getFile(String name, String extension);
}
