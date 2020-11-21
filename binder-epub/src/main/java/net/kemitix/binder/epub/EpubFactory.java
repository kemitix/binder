package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.impl.OpfCreatorDefault;
import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class EpubFactory {

    private final BinderConfig binderConfig;
    private final HtmlManuscript htmlManuscript;
    private final EpubSectionRenderer epubSectionRenderer;
    private final Content stylesheet;

    @Inject
    public EpubFactory(
            BinderConfig binderConfig,
            HtmlManuscript htmlManuscript,
            @Named Content stylesheet,
            EpubSectionRenderer epubSectionRenderer
    ) {
        this.binderConfig = binderConfig;
        this.htmlManuscript = htmlManuscript;
        this.epubSectionRenderer = epubSectionRenderer;
        this.stylesheet = stylesheet;
    }

    @Produces
    @ApplicationScoped
    public EpubBook create() {
        Metadata metadata = htmlManuscript.getMetadata();
        EpubBook epub = createEpub(metadata, htmlManuscript.sections());
        epub.addContent(stylesheet);
        epub.addCoverImage(coverImage(metadata.getCover()),
                "image/jpeg", "cover.jpg");
        epub.addTextContent("Cover", "cover.html",
                "<img src=\"cover.jpg\" style=\"height:100%\"/>");
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .flatMap(epubSectionRenderer::render)
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

    private EpubBook createEpub(
            Metadata metadata,
            Stream<HtmlSection> sections
    ) {
        String language = metadata.getLanguage();
        log.info("Language: " + language);
        String id = Objects.requireNonNull(metadata.getId(), "metadata id");
        log.info("Id: " + id);
        String title = "%s - Issue %d".formatted(
                metadata.getTitle(), metadata.getIssue());
        log.info("Title: " + title);
        String editor = metadata.getEditor();
        log.info("Editor: " + editor);
        EpubBook epubBook = new EpubBook(language, id, title, editor);
        OpfCreatorDefault opfCreator = ((OpfCreatorDefault) epubBook.getEpubCreator().getOpfCreator());
        String creators = sections
                .map(Section::getAuthor)
                .filter(Objects::nonNull)
                .map("<dc:creator>%s</dc:creator>"::formatted)
                .collect(Collectors.joining());
        opfCreator.setOpfXML("""
            <?xml version="1.0" encoding="UTF-8"?>
                <package xmlns="http://www.idpf.org/2007/opf" version="3.0" unique-identifier="uid">
                    <metadata xmlns:dc="http://purl.org/dc/elements/1.1/">
                        <dc:identifier id="uid"></dc:identifier>
                        <dc:title></dc:title>
                        <dc:language></dc:language>
                        <dc:description>%6$s</dc:description>
                        <dc:date>%1$s</dc:date>
                        <dc:publisher>%2$s</dc:publisher>
                        <dc:contributor id='editor-id'>%2$s</dc:contributor>
                        <meta property="role" refines="#editor-id">Editor</meta>
                        <meta property="belongs-to-collection" id="collection-id">%3$s</meta>
                        <meta property="collection-type" refines="#collection-id">series</meta>
                        <meta property="group-position" refines="#collection-id">%4$s</opf:meta>
                        <meta property="dcterms:modified"></meta>
                        %5$s
                    </metadata>
                    <manifest>
                    </manifest>
                    <spine>
                    </spine>
                </package>
                """
                .formatted(
                        metadata.getDate(),
                        metadata.getEditor(),
                        title,
                        metadata.getIssue(),
                        creators,
                        metadata.getDescription()
                ));
        return epubBook;
    }
}
