package net.kemitix.binder.app;

import lombok.Getter;

import java.io.File;

/**
 * Thrown when directory in the BINDER_DIR environment variable is missing.
 */
@Getter
public class MissingBinderDirectory extends IllegalArgumentException {

    public MissingBinderDirectory(File directory) {
        super("The binder directory, %s, is missing".formatted(directory));
    }

}
