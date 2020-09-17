package net.kemitix.binder.app;

import coza.opencollab.epub.creator.model.EpubBook;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Writes the Epub file to disk
 */
@ApplicationScoped
public class EpubWriter {

    private final BinderConfig binderConfig;

    @Inject
    public EpubWriter(BinderConfig binderConfig) {
        this.binderConfig = binderConfig;
    }

    public void write(EpubBook epubBook) {
        String epubFile = binderConfig.getEpubFile().getAbsolutePath();
        try {
            epubBook.writeToFile(epubFile);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error creating epub file %s: %s",
                    epubFile, e.getMessage()), e);
        }
    }

}
