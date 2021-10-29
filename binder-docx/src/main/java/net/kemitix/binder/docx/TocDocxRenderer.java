package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;
import net.kemitix.binder.spi.TocSections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class TocDocxRenderer
        implements DocxRenderer, TocSections,
        AggregateRenderer<DocxTocItemRenderer, Section, Object, DocxRenderHolder> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<DocxTocItemRenderer> tocItemRenderers;
    private final DocxImageFacade docxImage;

    @Inject
    public TocDocxRenderer(
            HtmlManuscript htmlManuscript,
            Instance<DocxTocItemRenderer> tocItemRenderers,
            DocxImageFacade docxImage
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
        this.docxImage = docxImage;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.toc) || section.isType(Section.Type.tocoriginals);
    }

    @Override
    public Stream<DocxContent> render(Section section, Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        final Function<HtmlSection, Stream<?>> renderSection = s ->
                findRenderer(s, tocItemRenderers)
                        .render(s, context);

        Predicate<HtmlSection> isOriginal = HtmlSection::isOriginal;
        Predicate<HtmlSection> isReprint = isOriginal.negate();

        var originals = stories(htmlManuscript, isOriginal).size();
        var reprints = stories(htmlManuscript, isReprint).size();

        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                section.getTitle(),// "Contents"
                                FontSize.of(240), docx)));
        contents.add(docx.textParagraph(""));

        if (originals > 0 && reprints == 0) {
            singleIssueToc(renderSection, docx)
                    .forEachOrdered(contents::add);
        } else if (reprints > 0) {
            yearsCollectionToc(renderSection, docx)
                    .forEachOrdered(contents::add);
        } else {
            throw new RuntimeException("No stories Found");
        }

        return Stream.of(
                new DocxContent(
                        section.getName(),
                        docx.finaliseTitlePage(context, contents))
        );
    }

    private Stream<Object> singleIssueToc(
            Function<HtmlSection, Stream<?>> renderSection,
            DocxFacade docx
    ) {
        var stream = Stream.builder();

        var lastCount = new AtomicInteger(0);
        for (Section.Genre genre : Section.Genre.values()) {
            var stories = stories(htmlManuscript, genre);
            if (lastCount.get() > 0 && stories.size() > 0) stream.add(docx.pageBreak());
            genreToc(genre, stories, renderSection, docx, stream);
            lastCount.set(stories.size());
        }

        return stream.build();
    }

    private Stream<Object> yearsCollectionToc(
            Function<HtmlSection, Stream<?>> renderSection,
            DocxFacade docx
    ) {
        var year = htmlManuscript.getMetadata().getIssue();
        var stream = Stream.builder();

        Predicate<HtmlSection> isOriginal = HtmlSection::isOriginal;
        Predicate<HtmlSection> isReprint = isOriginal.negate();

        // Years Collection

        var first = true;
        for (Section.Genre genre : Section.Genre.values()) {
            var stories = stories(htmlManuscript, isReprint, genre);
            if (stories.isEmpty()) continue;
            if (!first) stream.add(docx.pageBreak());
            stream.add(
                    docx.drawings(
                            docxImage.textImages("The " + year + " Collection",
                                    FontSize.of(180), docx)));
            genreToc(genre, stories, renderSection, docx, stream);
            first = false;
        }

        // Bonus Original
        stream.add(docx.pageBreak());
        stream.add(
                docx.drawings(
                        docxImage.textImages("The Bonus Collection",
                                FontSize.of(180), docx)));

        for (Section.Genre genre : Section.Genre.values()) {
            stream.add(docx.textParagraph(""));
            var stories = stories(htmlManuscript, isOriginal, genre);
            genreToc(genre, stories, renderSection, docx, stream);
        }

        return stream.build();
    }

    @Override
    public Stream<HtmlSection> stories(HtmlManuscript htmlManuscript) {
        return tocSections(htmlManuscript, HtmlSection::isDocx)
                .filter(HtmlSection::isStory);
    }

    private void genreToc(
            Section.Genre genre,
            List<HtmlSection> sections,
            Function<HtmlSection, Stream<?>> renderSection,
            DocxFacade docx,
            Stream.Builder<Object> outputStream
    ) {
        log.info("Genre ToC - Sections: " + sections.size());
        if (sections.isEmpty()) return;

        // header
        outputStream.add(
                docx.drawings(
                        docxImage.textImages(genre.toString(),
                                FontSize.of(120), docx)));
        // items
        sections.stream()
                .flatMap(renderSection)
                .forEach(outputStream::add);
    }

}
