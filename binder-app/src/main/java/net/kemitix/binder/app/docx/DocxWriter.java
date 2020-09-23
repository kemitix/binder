package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.BinderConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DocxWriter {

    private final BinderConfig binderConfig;

    @Inject
    public DocxWriter(BinderConfig binderConfig) {
        this.binderConfig = binderConfig;
    }

    public void write(DocxBook docxBook) {
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
