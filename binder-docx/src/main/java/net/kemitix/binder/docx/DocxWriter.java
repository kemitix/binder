package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;
import org.docx4j.wml.ObjectFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@Log
@ApplicationScoped
public class DocxWriter
        implements ManuscriptWriter {

    private final BinderConfig binderConfig;
    private final Metadata metadata;
    private final MdManuscript mdManuscript;
    private final DocxMdRenderer docxMdRenderer;

    @Inject
    public DocxWriter(
            BinderConfig binderConfig,
            Metadata metadata,
            MdManuscript mdManuscript,
            DocxMdRenderer docxMdRenderer
    ) {
        this.binderConfig = binderConfig;
        this.metadata = metadata;
        this.mdManuscript = mdManuscript;
        this.docxMdRenderer = docxMdRenderer;
    }

    @Override
    public ResultVoid write() {
        return doWrite()
                .run(createWriteDocxEnv(binderConfig, metadata, mdManuscript, docxMdRenderer));
    }

    private static WriteDocxEnv createWriteDocxEnv(
            final BinderConfig binderConfig,
            final Metadata metadata,
            final MdManuscript mdManuscript,
            final DocxMdRenderer docxMdRenderer
    ) {
        DocxFacade docxFacade = new DocxFacade(metadata);
        return new WriteDocxEnv() {
            @Override
            public Logger log() {
                return DocxWriter.log;
            }

            @Override
            public String file() {
                return binderConfig.getDocxFile().getAbsolutePath();
            }

            @Override
            public DocxFacade docx() {
                return docxFacade;
            }

            @Override
            public Metadata metadata() {
                return metadata;
            }

            @Override
            public MdManuscript mdManuscript() {
                return mdManuscript;
            }

            @Override
            public ObjectFactory factory() {
                return docxFacade.factory();
            }

            @Override
            public DocxMdRenderer docxMdRenderer() {
                return docxMdRenderer;
            }
        };
    }

    private static Reader<WriteDocxEnv, ResultVoid> doWrite() {
        return env -> {
            var log = env.log();
            return Result.ok(env.file())
                    .peek(docxFile -> log.info("Writing: " + docxFile))
                    .thenWithV(docxFile -> () ->
                            DocxManuscript.writeToFile(docxFile, env.docx())
                                    .run(createDocxManuscriptEnv(env))
                                    .onSuccess(() -> log.info("Wrote: " + docxFile)))
                    .onError(MarkdownConversionException.class, e -> {
                        log.severe(e.getMessage());
                        log.severe("Node: " + e.getNode());
                        log.severe("Context: " + e.getContext());
                        log.severe("Content: " + e.getContent());
                    });
        };
    }

    private static DocxManuscript.DocxManuscriptEnv createDocxManuscriptEnv(WriteDocxEnv env) {
        return new DocxManuscript.DocxManuscriptEnv() {
            @Override
            public Metadata metadata() {
                return env.metadata();
            }

            @Override
            public ObjectFactory factory() {
                return env.factory();
            }

            @Override
            public MdManuscript mdManuscript() {
                return env.mdManuscript();
            }

            @Override
            public DocxMdRenderer docxMdRenderer() {
                return env.docxMdRenderer();
            }
        };
    }

    interface WriteDocxEnv {
        Logger log();
        String file();
        DocxFacade docx();
        Metadata metadata();
        MdManuscript mdManuscript();
        ObjectFactory factory();
        DocxMdRenderer docxMdRenderer();
    }
}
