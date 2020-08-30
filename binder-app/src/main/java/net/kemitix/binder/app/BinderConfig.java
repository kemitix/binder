package net.kemitix.binder.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface BinderConfig {
    File getScanDirectory();

    default File getFile(String name, String extension) {
        return getScanDirectory().toPath()
                .resolve(String.format("%s.%s", name, extension))
                .toFile();
    }

    default File getBinderOutputDirectory() {
        return makeDirectory(getScanDirectory().toPath().resolve("binder"))
                .toFile();
    }

    private Path makeDirectory(Path path) {
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            return path;
        }
        if (dir.exists()) {
            throw new RuntimeException(String.format(
                    "Error output directory should be a writable directory: %s",
                    path));
        }
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Error creating output directory: %s", path), e);
        }
        return path;
    }


    default File getEpubFile() {
        return getBinderOutputDirectory().toPath()
                .resolve("binder.epub")
                .toFile();
    }

}
