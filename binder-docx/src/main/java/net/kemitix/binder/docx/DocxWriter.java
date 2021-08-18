package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

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
        return Result.ok(binderConfig.getDocxFile())
                .map(File::getAbsolutePath)
                .flatMap(this::writeDocxFile)
                .onError(MarkdownConversionException.class, e -> {
                    log.severe(e.getMessage());
                    log.severe("Node: " + e.getNode());
                    log.severe("Context: " + e.getContext());
                    log.severe("Content: " + e.getContent());
                })
                ;
    }

    private Result<Void> writeDocxFile(String docxFile) {
        return Result.ofVoid(() -> {
            log.info("Writing: " + docxFile);
            var docx = new DocxFacade(metadata);
            docxManuscript.writeToFile(docxFile, docx);
            log.info("Wrote: " + docxFile);
        });
    }
}
