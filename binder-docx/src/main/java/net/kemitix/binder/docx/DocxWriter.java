package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.MarkdownConversionException;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.BinderException;
import net.kemitix.binder.spi.ManuscriptWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class DocxWriter
        implements ManuscriptWriter {

    private final BinderConfig binderConfig;
    private final DocxManuscript docxManuscript;

    @Inject
    public DocxWriter(
            BinderConfig binderConfig,
            DocxManuscript docxManuscript
    ) {
        this.binderConfig = binderConfig;
        this.docxManuscript = docxManuscript;
    }

    @Override
    public void write() {
        String docxFile = binderConfig.getDocxFile().getAbsolutePath();
        log.info("Writing: " + docxFile);
        try {
            docxManuscript.writeToFile(docxFile);
        } catch (MarkdownConversionException e) {
            log.severe(e.getMessage());
            log.severe("Node: " + e.getNode());
            log.severe("Context: " + e.getContext());
            log.severe("Content: " + e.getContent());
        }
        log.info("Wrote: " + docxFile);
    }
}
