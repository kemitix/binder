package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.markdown.MarkdownOutputException;
import net.kemitix.binder.spi.*;

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
            throw new BinderException(
                    """
                            %s
                            Node: %s
                            %s
                            Content: %s
                            """.formatted(e.getMessage(), e.getNode(),
                            e.getContext(), e.getContent()),
                    e
            );
        } catch (ManuscriptFormatException e) {
            throw new BinderException(String.format(
                    "Error creating epub file %s", epubFile), e);
        } catch (MarkdownOutputException e) {
            throw new BinderException(String.format(
                    "Error creating epub file %s: %s [%s]", epubFile, e.getMessage(), e.getOutput()), e);
        }
        log.info("Wrote: " + epubFile);
    }

}
