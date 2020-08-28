package net.kemitix.binder.app;

import lombok.Getter;

import java.io.File;

/**
 * Thrown when the binder.yaml file is not found where expected.
 */
@Getter
public class MissingBinderYamlFile extends RuntimeException {

    private final File file;

    public MissingBinderYamlFile(File file) {
        this.file = file;
    }

}
