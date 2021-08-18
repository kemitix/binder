package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.markdown.MarkdownOutputException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.BinderException;
import net.kemitix.binder.spi.ManuscriptFormatException;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

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

    public ResultVoid write() {
        return Result.of(binderConfig::getEpubFile)
                .map(File::getAbsolutePath)
                .flatMapV(this::writeEpubFile)
                .onError(MarkdownConversionException.class, e -> {
                    log.severe(e.getMessage());
                    log.severe("Node: " + e.getNode());
                    log.severe("Context: " + e.getContext());
                    log.severe("Content: " + e.getContent());
                });
    }

    private ResultVoid writeEpubFile(String epubFile) {
        return Result.ofVoid(() -> {
            log.info("Writing: " + epubFile);
            epubBook.writeToFile(epubFile);
            log.info("Wrote: " + epubFile);
        }).onError(ManuscriptFormatException.class, e ->
                new BinderException(String.format(
                        "Error creating epub file %s", epubFile), e)
        ).onError(MarkdownOutputException.class, e ->
                new BinderException(String.format(
                        "Error creating epub file %s: %s [%s]", epubFile, e.getMessage(), e.getOutput()), e)
        );
    }

}
