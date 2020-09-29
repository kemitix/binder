package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import net.kemitix.binder.app.BinderConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Writes the Epub file to disk
 */
@ApplicationScoped
public class EpubWriter {

    private final BinderConfig binderConfig;
    private final EpubBook epubBook;

    @Inject
    public EpubWriter(
            BinderConfig binderConfig,
            EpubBook epubBook) {
        this.binderConfig = binderConfig;
        this.epubBook = epubBook;
    }

    public void write() {
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
