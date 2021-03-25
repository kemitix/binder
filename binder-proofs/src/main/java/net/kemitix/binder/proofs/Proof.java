package net.kemitix.binder.proofs;

import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxManuscript;
import net.kemitix.binder.spi.Metadata;

import java.nio.file.Paths;
import java.util.Collections;

public class Proof
        extends DocxManuscript {

    private final DocxContent docxContent;

    public Proof(
            DocxContent docxContent,
            DocxFacade docx,
            Metadata metadata
    ) {
        super(Collections.singletonList(docxContent), docx, metadata);
        this.docxContent = docxContent;
    }

    @Override
    public void writeToFile(String dirName) {
        var fileName = Paths.get(dirName)
                .resolve(docxContent.getSectionName().getValue())
                .toFile()
                .getAbsolutePath();
        super.writeToFile(fileName);
    }
}
