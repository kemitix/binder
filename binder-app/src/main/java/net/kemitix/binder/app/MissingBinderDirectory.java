package net.kemitix.binder.app;

import lombok.Getter;

import java.io.File;

/**
 * Thrown when the BINDER_DIRECTORY is missing.
 */
@Getter
public class MissingBinderDirectory extends RuntimeException {

    private final File directory;

    public MissingBinderDirectory(File directory) {
        this.directory = directory;
    }

}
