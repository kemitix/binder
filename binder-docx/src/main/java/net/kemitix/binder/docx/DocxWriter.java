package net.kemitix.binder.docx;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DocxWriter implements ManuscriptWriter {

    private final BinderConfig binderConfig;
    private final DocxBook docxBook;

    @Inject
    public DocxWriter(
            BinderConfig binderConfig,
            DocxBook docxBook
    ) {
        this.binderConfig = binderConfig;
        this.docxBook = docxBook;
    }

    public void write() {
        String docxFile = binderConfig.getDocxFile().getAbsolutePath();
        try {
            docxBook.writeToFile(docxFile);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error creating docx file %s: %s",
                    docxFile, e.getMessage()), e);
        }
    }

}
