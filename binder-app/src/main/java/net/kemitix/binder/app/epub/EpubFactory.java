package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.Metadata;
import net.kemitix.binder.app.Section;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Log
@ApplicationScoped
public class EpubFactory {

    private final BinderConfig binderConfig;
    private final HtmlManuscript htmlManuscript;
    private final EpubContentFactory epubContentFactory;

    @Inject
    public EpubFactory(
            BinderConfig binderConfig,
            HtmlManuscript htmlManuscript,
            EpubContentFactory epubContentFactory
    ) {
        this.binderConfig = binderConfig;
        this.htmlManuscript = htmlManuscript;
        this.epubContentFactory = epubContentFactory;
    }

    @Produces
    @ApplicationScoped
    public EpubBook create() {
        Metadata metadata = htmlManuscript.getMetadata();
        EpubBook epub = createEpub(metadata);
        epub.addCoverImage(coverImage(metadata.getCover()),
                "image/jpeg", "cover.jpg");
        epub.addTextContent("Cover", "cover.html", "<img src=\"cover.jpg\" style=\"height:100%\"/>");
        htmlManuscript.getHtmlSections()
                .entrySet()
                .stream()
                .filter(includeInEpub(htmlManuscript.getContents()))
                .map(e -> epubContentFactory.create(e.getKey(), e.getValue()))
                .forEach(epub::addContent);
        return epub;
    }

    private Predicate<Map.Entry<String, String>> includeInEpub(List<Section> contents) {
        return entry -> contents.stream()
                .filter(Section::isEpub)
                .map(Section::getName)
                .anyMatch(name -> name.equals(entry.getKey()));
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
