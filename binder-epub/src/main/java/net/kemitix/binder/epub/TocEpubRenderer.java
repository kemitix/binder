package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.markdown.DocumentNodeHandler;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;
import net.kemitix.binder.spi.TocSections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Epub
@ApplicationScoped
public class TocEpubRenderer
        implements EpubRenderer, TocSections,
        AggregateRenderer<EpubTocItemRenderer, HtmlSection, String, EpubRenderHolder> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<EpubTocItemRenderer> tocItemRenderers;
    private final DocumentNodeHandler<String, EpubRenderHolder> documentNodeHandler;

    @Inject
    public TocEpubRenderer(
            HtmlManuscript htmlManuscript,
            Instance<EpubTocItemRenderer> tocItemRenderers,
            @Epub DocumentNodeHandler<String, EpubRenderHolder> documentNodeHandler
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
        this.documentNodeHandler = documentNodeHandler;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.toc) || section.isType(Section.Type.tocoriginals);
    }

    @Override
    public Stream<Content> render(
            HtmlSection htmlSection,
            Context<EpubRenderHolder> epubRenderHolder
    ) {
        Predicate<HtmlSection> isOriginal = HtmlSection::isOriginal;
        Predicate<HtmlSection> isReprint = isOriginal.negate();

        var originals = stories(htmlManuscript, isOriginal).size();
        var reprints = stories(htmlManuscript, isReprint).size();

        StringBuilder htmlBuilder = new StringBuilder();

        if (originals > 0 && reprints == 0) {
            singleIssueToc(epubRenderHolder)
                    .forEachOrdered(htmlBuilder::append);
        } else if (reprints > 0) {
            yearsCollectionToc(epubRenderHolder)
                    .forEachOrdered(htmlBuilder::append);
        } else {
            throw new RuntimeException("No stories Found");
        }

        String body = htmlBuilder.toString();
        String href = htmlSection.getHref();
        String html = documentNodeHandler
                .documentBody(
                        htmlSection.getTitle(),
                        Stream.of(body))
                .collect(Collectors.joining());
        return Stream.of(
                new Content(href, html.getBytes(StandardCharsets.UTF_8))
        );
    }

    private Stream<String> singleIssueToc(Context<EpubRenderHolder> epubRenderHolder) {
        var stream = Stream.<String>builder();

        var stories = stories(htmlManuscript).collect(Collectors.toList());
        genreToc(stories, epubRenderHolder)
                .forEach(stream::add);

        return stream.build();
    }

    private Stream<String> yearsCollectionToc(Context<EpubRenderHolder> epubRenderHolder) {
        var year = htmlManuscript.getMetadata().getIssue();
        var stream = Stream.<String>builder();

        Predicate<HtmlSection> isOriginal = HtmlSection::isOriginal;
        Predicate<HtmlSection> isReprint = isOriginal.negate();

        // Years Collection
        for (Section.Genre genre : Section.Genre.values()) {
            var stories = stories(htmlManuscript, isReprint, genre);
            if (stories.isEmpty()) continue;
            stream.add("""
            <h2 style="text-align: center">
            The %s Stories / %s
            </h2>
            """.formatted(year, genre.toString()));
            genreToc(stories, epubRenderHolder)
                    .forEach(stream::add);
        }

        // Bonus Original
        for (Section.Genre genre : Section.Genre.values()) {
            var stories = stories(htmlManuscript, isOriginal, genre);
            if (stories.isEmpty()) continue;
            stream.add("""
            <h2 style="text-align: center">
            The Bonus Stories / %s
            </h2>
            """.formatted(genre.toString()));
            genreToc(stories, epubRenderHolder)
                    .forEach(stream::add);
        }

        return stream.build();
    }

    private Stream<String> genreToc(
            List<HtmlSection> stories,
            Context<EpubRenderHolder> epubRenderHolder
    ) {
        if (stories.isEmpty()) return Stream.empty();

        var stream = Stream.<String>builder();

        stream.add("<ul>");
        stories.stream()
                .flatMap(story ->
                        findRenderer(story, tocItemRenderers)
                                .render(story, epubRenderHolder))
                .forEach(stream::add);
        stream.add("</ul>");

        return stream.build();
    }

    private Predicate<? super HtmlSection> isTocOrTocOriginals(Section section) {
        return thisSection -> section.isType(Section.Type.toc) || (
                section.isType(Section.Type.tocoriginals) && thisSection.isOriginal()
        );
    }

    @Override
    public Stream<HtmlSection> stories(HtmlManuscript htmlManuscript) {
        return tocSections(htmlManuscript, HtmlSection::isEpub)
                .filter(HtmlSection::isStory);
    }

}
