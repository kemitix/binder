package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Log
@ApplicationScoped
public class EpubFactory {

    private final BinderConfig binderConfig;
    private final HtmlManuscript htmlManuscript;
    private final EpubSectionRenderer epubSectionRenderer;

    @Inject
    public EpubFactory(
            BinderConfig binderConfig,
            HtmlManuscript htmlManuscript,
            EpubSectionRenderer epubSectionRenderer
    ) {
        this.binderConfig = binderConfig;
        this.htmlManuscript = htmlManuscript;
        this.epubSectionRenderer = epubSectionRenderer;
    }

    @Produces
    @ApplicationScoped
    public EpubBook create() {
        Metadata metadata = htmlManuscript.getMetadata();
        EpubBook epub = createEpub(metadata);
        epub.addCoverImage(coverImage(metadata.getCover()),
                "image/jpeg", "cover.jpg");
        epub.addTextContent("Cover", "cover.html",
                "<img src=\"cover.jpg\" style=\"height:100%\"/>");
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .map(epubSectionRenderer::render)
                .forEach(epub::addContent);
        return epub;
    }

    private byte[] coverImage(String cover) {
        Path coverPath = binderConfig.getScanDirectory().toPath().resolve(cover);
        try {
            return Files.readAllBytes(coverPath);
        } catch (IOException e) {
            throw new RuntimeException(String.format(
                    "Error loading Cover image: %s", coverPath),
                    e);
        }
    }

    private EpubBook createEpub(Metadata metadata) {
        String language = metadata.getLanguage();
        log.info("Language: " + language);
        String id = Objects.requireNonNull(metadata.getId(), "metadata id");
        log.info("Id: " + id);
        String title = metadata.getTitle();
        log.info("Title: " + title);
        String editor = metadata.getEditor();
        log.info("Editor: " + editor);
        return new EpubBook(language, id, title, editor);
    }
}
