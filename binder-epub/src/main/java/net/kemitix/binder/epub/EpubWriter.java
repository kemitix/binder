package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.BinderException;
import net.kemitix.binder.spi.ManuscriptFormatException;
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

    @SneakyThrows
    public void write() {
        String epubFile = binderConfig.getEpubFile().getAbsolutePath();
        log.info("Writing: " + epubFile);
        try {
            epubBook.writeToFile(epubFile);
        } catch (MarkdownConversionException e) {
            log.severe(e.getMessage());
            log.severe("Node: " + e.getNode());
            log.severe("Context: " + e.getContext());
            log.severe("Content: " + e.getContent());
        } catch (ManuscriptFormatException e) {
            throw new BinderException(String.format(
                    "Error creating epub file %s", epubFile), e);
        }
        log.info("Wrote: " + epubFile);
    }

}
