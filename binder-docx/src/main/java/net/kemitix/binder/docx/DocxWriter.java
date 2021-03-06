package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
    public void write() {
        String docxFile = binderConfig.getDocxFile().getAbsolutePath();
        log.info("Writing: " + docxFile);
        var docx = new DocxFacade(metadata);
        try {
            docxManuscript.writeToFile(docxFile, docx);
        } catch (MarkdownConversionException e) {
            log.severe(e.getMessage());
            log.severe("Node: " + e.getNode());
            log.severe("Context: " + e.getContext());
            log.severe("Content: " + e.getContent());
        }
        log.info("Wrote: " + docxFile);
    }
}
