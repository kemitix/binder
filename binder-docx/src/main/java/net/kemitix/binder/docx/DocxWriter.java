package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@Log
@ApplicationScoped
public class DocxWriter
        implements ManuscriptWriter {

    private final BinderConfig binderConfig;
    private final DocxManuscript docxManuscript;
    private final Metadata metadata;

    @Inject
    public DocxWriter(
            BinderConfig binderConfig,
            DocxManuscript docxManuscript,
            Metadata metadata
    ) {
        this.binderConfig = binderConfig;
        this.docxManuscript = docxManuscript;
        this.metadata = metadata;
    }

    @Override
    public ResultVoid write() {
        DocxFacade docx = new DocxFacade(metadata);
        return doWrite().run(new WriteDocxEnv() {
            @Override
            public Logger log() {
                return null;
            }

            @Override
            public String file() {
                return binderConfig.getDocxFile().getAbsolutePath();
            }

            @Override
            public DocxManuscript manuscript() {
                return docxManuscript;
            }

            @Override
            public DocxFacade docx() {
                return docx;
            }
        });
    }

    private static Reader<WriteDocxEnv, ResultVoid> doWrite() {
        return env -> Result.ok(env.file())
                .peek(docxFile -> env.log().info("Writing: " + docxFile))
                .thenWithV(f -> () -> env.manuscript().writeToFile(f, env.docx())
                        .onSuccess(() -> env.log().info("Wrote: " + f)))
                .onError(MarkdownConversionException.class, e -> {
                    env.log().severe(e.getMessage());
                    env.log().severe("Node: " + e.getNode());
                    env.log().severe("Context: " + e.getContext());
                    env.log().severe("Content: " + e.getContent());
                });
    }

    interface WriteDocxEnv {
        Logger log();
        String file();
        DocxManuscript manuscript();
        DocxFacade docx();
    }
}
