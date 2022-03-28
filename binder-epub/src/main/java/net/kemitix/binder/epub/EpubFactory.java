package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.api.MetadataItem;
import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import coza.opencollab.epub.creator.model.Landmark;
import coza.opencollab.epub.creator.model.TocLink;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.Context;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        epub.setAutoToc(false);
        epub.addContent(stylesheet);
        Content cover = addCover(metadata, epub);
        addSections(epub);
        addTableOfContents(epub, cover);
        addLandmarks(epub, cover);
        return epub;
    }

    private void addSections(EpubBook epub) {
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .flatMap((HtmlSection htmlSection) -> {
                    Context<EpubRenderHolder> context =
                            Context.create(
                                    htmlSection,
                                    EpubRenderHolder.create());
                    return epubSectionRenderer.render(
                            htmlSection,
                            context);
                })
                .forEach(epub::addContent);
    }

    private Content addCover(Metadata metadata, EpubBook epub) {
        epub.addCoverImage(coverImage(metadata.getCover()),
                "image/jpeg", "cover.jpg");
        return epub.addTextContent("Cover", "cover.xhtml",
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

    private void addLandmarks(EpubBook epub, Content cover) {
        List<Landmark> landmarks = new ArrayList<>();
        landmarks.add(landmark(Section.LandmarkType.cover, cover.getHref()));
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .filter(HtmlSection::isLandmark)
                .forEach(section ->
                        landmarks.add(
                                landmark(section.getLandmarkType(),
                                        section.getHref())));
        epub.setLandmarks(landmarks);
    }

    private Landmark landmark(Section.LandmarkType type, String href) {
        Landmark landmark = new Landmark();
        landmark.setType(type.toString());
        landmark.setHref(href);
        landmark.setTitle("&nbps;");
        return landmark;
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
        var language = metadata.getLanguage();
        var id = Objects.requireNonNull(metadata.getId(), "metadata id");
        var title = "%s Issue %d".formatted(
                metadata.getTitle(), metadata.getIssue());
        var editor = metadata.getEditor();
        var epubBook = new EpubBook(language, id, title, editor);
        var mib = MetadataItem.builder();
        var meta = mib.name("meta");
        var date = Objects.requireNonNull(metadata.getDate(), "metadata date");
        var modified = Optional.ofNullable(metadata.getModified()).orElseGet(() -> {
            log.warning("Modified date not specified, defaulting to publication date");
            return date;
        });
        var metadataItems = Arrays.asList(
                mib.name("meta").property("dcterms:modified").value(modified + "T00:00:00+00:00"),
                mib.name("dc:description").value(metadata.getDescription()),
                mib.name("dc:contributor").id("editor-id").value(metadata.getEditor()),
                mib.name("dc:date").value(date + "T00:00:00+00:00"),
                mib.name("dc:publisher").value(editor),
                meta.property("role").refines("#editor-id").value("Editor"),
                meta.id("collection-id").property("belongs-to-collection").value(metadata.getTitle()),
                meta.property("collection-type").refines("#collection-id").value("series"),
                meta.property("group-position").refines("#collection-id").value(Integer.toString(metadata.getIssue()))
        );
        metadataItems.forEach(epubBook::addMetadata);
        sections.map(Section::getAuthor)
                .filter(Objects::nonNull)
                .map(mib.name("dc:creator")::value)
                .forEach(epubBook::addMetadata);
        return epubBook;
    }
}
