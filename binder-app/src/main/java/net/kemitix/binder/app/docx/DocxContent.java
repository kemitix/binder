package net.kemitix.binder.app.docx;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.File;

public class DocxContent {
    private final WordprocessingMLPackage wordMLPackage;

    public DocxContent(WordprocessingMLPackage wordMLPackage) {
        this.wordMLPackage = wordMLPackage;
    }

    public void save(String file) {
        try {
            wordMLPackage.save(new File(file));
        } catch (Docx4JException e) {
            throw new RuntimeException(
                    "Error saving file: %s".formatted(file), e);
        }
    }
}
