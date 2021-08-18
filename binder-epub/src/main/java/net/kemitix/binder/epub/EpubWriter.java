package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.markdown.MarkdownOutputException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.BinderException;
import net.kemitix.binder.spi.ManuscriptFormatException;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

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
        return doWrite().run(new WriteEpubEnv() {
            @Override
            public String file() {
                return binderConfig.getEpubFile().getAbsolutePath();
            }

            @Override
            public EpubBook book() {
                return epubBook;
            }

            @Override
            public Logger log() {
                return log;
            }
        });
    }

    private static Reader<WriteEpubEnv , ResultVoid> doWrite() {
        return env -> Result
                .ofVoid(() -> env.log().info("Writing: " + env.file()))
                .andThen(() -> env.book().writeToFile(env.file()))
                .andThen(() -> env.log().info("Wrote: " + env.file()))

                .onError(ManuscriptFormatException.class, e ->
                        new BinderException(String.format(
                                "Error creating epub file %s", env.file()), e))

                .onError(MarkdownOutputException.class, e ->
                        new BinderException(String.format(
                                "Error creating epub file %s: %s [%s]",
                                env.file(), e.getMessage(), e.getOutput()), e))

                .onError(MarkdownConversionException.class, e -> {
                    env.log().severe(e.getMessage());
                    env.log().severe("Node: " + e.getNode());
                    env.log().severe("Context: " + e.getContext());
                    env.log().severe("Content: " + e.getContent());
                });
    }

    interface WriteEpubEnv {
        String file();
        EpubBook book();
        Logger log();
    }

}
