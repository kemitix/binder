package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.MdManuscript;
import net.kemitix.binder.app.Metadata;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log
@ApplicationScoped
public class EpubFactory {

    private final BinderConfig binderConfig;
    private final MdManuscript mdManuscript;
    private final EpubContentFactory epubContentFactory;

    @Inject
    public EpubFactory(
            BinderConfig binderConfig,
            MdManuscript mdManuscript,
            EpubContentFactory epubContentFactory
    ) {
        this.binderConfig = binderConfig;
        this.mdManuscript = mdManuscript;
        this.epubContentFactory = epubContentFactory;
    }

    public EpubBook create() {
        Metadata metadata = mdManuscript.getMetadata();
        EpubBook epub = createEpub(metadata);
        epub.addCoverImage(coverImage(metadata.getCover()),
                "image/jpeg", "cover.jpg");
        epub.addTextContent("Cover", "cover.html", "<img src=\"cover.jpg\" style=\"height:100%\"/>");
        mdManuscript.getContents().stream()
                .map(epubContentFactory::create)
                .forEach(epub::addContent);
        return epub;
    }

    private byte[] coverImage(String cover) {
        Path coverPath = binderConfig.getScanDirectory().toPath()
                .resolve(cover);
        try {
            return Files.readAllBytes(coverPath);
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Error loading Cover image: %s", coverPath),
                    e);
        }
    }

    @NotNull
    private EpubBook createEpub(Metadata metadata) {
        String language = metadata.getLanguage();
        log.info("Language: " + language);
        String id = metadata.getId();
        log.info("Id: " + id);
        String title = metadata.getTitle();
        log.info("Title: " + title);
        String editor = metadata.getEditor();
        log.info("Editor: " + editor);
        return new EpubBook(language, id, title, editor);
    }
}
