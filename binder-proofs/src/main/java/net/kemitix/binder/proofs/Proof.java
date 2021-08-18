package net.kemitix.binder.proofs;

import lombok.SneakyThrows;
import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxFactory;
import net.kemitix.binder.docx.DocxManuscript;
import net.kemitix.binder.docx.DocxMdRenderer;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

public class Proof
        extends DocxManuscript {

    private final DocxContent docxContent;

    public Proof(
            DocxContent docxContent,
            Metadata metadata,
            MdManuscript mdManuscript,
            DocxMdRenderer docxMdRenderer
    )  {
        super(metadata, mdManuscript, docxMdRenderer);
        this.docxContent = docxContent;
    }

    @SneakyThrows
    @Override
    public ResultVoid writeToFile(String dirName, DocxFacade docx) {
        var dirPath = getDirPath(dirName);
        var filePath = getFilePath(dirPath);
        super.writeToFile(filePath, docx);
        return Result.ok();
    }

    private Path getDirPath(String dirName) throws IOException {
        var dirPath = Paths.get(dirName);
        if (!dirPath.toFile().exists()) {
            Files.createDirectory(dirPath);
        }
        return dirPath;
    }

    private String getFilePath(Path dirPath) {
        return dirPath
                .resolve(getFileName())
                .toFile()
                .getAbsolutePath();
    }

    private String getFileName() {
        return "%s.docx".formatted(docxContent.getSectionName().getValue());
    }

    @Override
    protected Collection<?> getContents(DocxFacade docx) {
        var contents = new DocxFactory()
                .create(getMdManuscript(), getDocxMdRenderer(), () -> docx);
        return contents.stream()
                .flatMap(docxContent -> docxContent.getContents().stream())
                .collect(Collectors.toList());
    }
}
