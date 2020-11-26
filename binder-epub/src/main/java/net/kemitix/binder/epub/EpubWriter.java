package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Writes the Epub file to disk
 */
@Log
@ApplicationScoped
public class EpubWriter implements ManuscriptWriter {

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
        log.info("Writing: " + epubFile);
        try {
            epubBook.writeToFile(epubFile);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error creating epub file %s: %s",
                    epubFile, e.getMessage()), e);
        }
        log.info("Wrote: " + epubFile);
    }

}
