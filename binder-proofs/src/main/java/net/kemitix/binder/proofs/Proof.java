package net.kemitix.binder.proofs;

import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxFactory;
import net.kemitix.binder.docx.DocxManuscript;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

public class Proof {

    public static Reader<ProofEnv, String> getTitle() {
        return e -> e.metadata().getTitle();
    }

    public static Reader<ProofEnv, ResultVoid> writeToFile(
            String dirName,
            DocxFacade docx
    ) {
        return env ->
                Result.of(() -> getDirPath(dirName))
                        .map(Proof::getFilePath)
                        .map(fp -> fp.run(env))
                        .flatMapV(filePath ->
                                DocxManuscript.writeToFile(filePath, docx)
                                        .run(env));
    }

    private static Path getDirPath(String dirName) throws IOException {
        var dirPath = Paths.get(dirName);
        if (!dirPath.toFile().exists()) {
            Files.createDirectory(dirPath);
        }
        return dirPath;
    }

    private static Reader<ProofEnv, String> getFilePath(Path dirPath) {
        return env -> dirPath
                .resolve(getFileName().run(env))
                .toFile()
                .getAbsolutePath();
    }

    private static Reader<ProofEnv, String> getFileName() {
        return env -> "%s.docx".formatted(
                env.docxContent().getSectionName().getValue());
    }

    private Reader<ProofEnv, Collection<?>> getContents(DocxFacade docx) {
        return env -> {
            var contents = new DocxFactory()
                    .create(env.mdManuscript(), env.docxMdRenderer(), () -> docx);
            return contents.stream()
                    .flatMap(docxContent -> docxContent.getContents().stream())
                    .collect(Collectors.toList());
        };
    }

    interface ProofEnv extends DocxManuscript.DocxManuscriptEnv {
        DocxContent docxContent();
    }
}
