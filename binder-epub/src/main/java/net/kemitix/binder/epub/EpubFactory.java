package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.api.MetadataItem;
import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import coza.opencollab.epub.creator.model.TocLink;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
        epub.setAutoToc(false);
        epub.addContent(stylesheet);
        Content cover = addCover(metadata, epub);
        addSections(epub);
        addTableOfContents(epub, cover);
        return epub;
    }

    private void addSections(EpubBook epub) {
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .flatMap(epubSectionRenderer::render)
                .forEach(epub::addContent);
    }

    private Content addCover(Metadata metadata, EpubBook epub) {
        epub.addCoverImage(coverImage(metadata.getCover()),
                "image/jpeg", "cover.jpg");
        return epub.addTextContent("Cover", "cover.html",
                "<img src=\"cover.jpg\" style=\"height:100%\"/>");
    }

    private void addTableOfContents(EpubBook epub, Content cover) {
        //TOC
        List<TocLink> tocLinks = epub.getTocLinks();
        epub.setTocLinks(tocLinks);
        tocLinks.add(new TocLink(cover.getHref(), "Cover", ""));
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .filter(HtmlSection::isToc)
                .forEach(section ->
                        tocLinks.add(new TocLink(section.getHref(), section.getTitle(), "")));
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
        String id = Objects.requireNonNull(metadata.getId(), "metadata id");
        String title = "%s Issue %d".formatted(
                metadata.getTitle(), metadata.getIssue());
        String editor = metadata.getEditor();
        EpubBook epubBook = new EpubBook(language, id, title, editor);
        MetadataItem.Builder mib = MetadataItem.builder();
        var meta = mib.name("meta");
        var metadataItems = Arrays.asList(
                mib.name("dc:description").value(metadata.getDescription()),
                mib.name("dc:contributor").id("editor-id").value(metadata.getEditor()),
                mib.name("dc:date").value(metadata.getDate()+"T00:00:00+00:00"),
                mib.name("dc:publisher").value(editor),
                meta.property("role").refines("#editor-id").value("Editor"),
                meta.id("collection-id").property("belongs-to-collection").value(metadata.getTitle()),
                meta.property("collection-type").refines("#collection-id").value("series"),
                meta.property("group-position").refines("#collection-id").value(Integer.toString(metadata.getIssue()))
        );
        metadataItems.forEach(epubBook::addMetadata);
        var dcCreator = mib.name("dc:creator");
        sections.map(Section::getAuthor)
                .filter(Objects::nonNull)
                .map(dcCreator::value)
                .forEach(epubBook::addMetadata);
        return epubBook;
    }
}
