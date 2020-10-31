package net.kemitix.binder.docx;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Path;

@ApplicationScoped
public class LegacyDocxWriter implements ManuscriptWriter {

    private final BinderConfig binderConfig;
    private final DocxBook docxBook;

    @Inject
    public LegacyDocxWriter(
            BinderConfig binderConfig,
            DocxBook docxBook
    ) {
        this.binderConfig = binderConfig;
        this.docxBook = docxBook;
    }

    public void write() {
        Path path = binderConfig.getDocxFile().toPath();
        String filename = "legacy-%s".formatted(path.getFileName());
        String docxFile = path.getParent().resolve(filename).toFile().getAbsolutePath();
        try {
            docxBook.writeToFile(docxFile);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error creating docx file %s: %s",
                    docxFile, e.getMessage()), e);
        }
    }

}
