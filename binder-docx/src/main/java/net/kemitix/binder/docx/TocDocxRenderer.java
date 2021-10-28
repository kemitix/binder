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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

        var scienceFiction = stories(htmlManuscript, Section.Genre.ScienceFiction);
        var fantasy = stories(htmlManuscript, Section.Genre.Fantasy);
        var scienceFantasy = stories(htmlManuscript, Section.Genre.ScienceFantasy);

        genreToc("Science Fiction", scienceFiction, renderSection, docx, stream);
        if (scienceFiction.size() > 0 && fantasy.size() > 0) stream.add(docx.pageBreak());
        genreToc("Fantasy", fantasy, renderSection, docx, stream);
        if (fantasy.size() > 0 && scienceFantasy.size() > 0) stream.add(docx.pageBreak());
        genreToc("Science Fantasy", scienceFantasy, renderSection, docx, stream);

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
        stream.add(
                docx.drawings(
                        docxImage.textImages("The " + year + " Collection",
                                FontSize.of(180), docx)));

        var scienceFiction = stories(htmlManuscript, isReprint, Section.Genre.ScienceFiction);
        var fantasy = stories(htmlManuscript, isReprint, Section.Genre.Fantasy);
        var scienceFantasy = stories(htmlManuscript, isReprint, Section.Genre.ScienceFantasy);

        genreToc("Science Fiction", scienceFiction, renderSection, docx, stream);
        if (scienceFiction.size() > 0 && fantasy.size() > 0) stream.add(docx.pageBreak());
        genreToc("Fantasy", fantasy, renderSection, docx, stream);
        if (fantasy.size() > 0 && scienceFantasy.size() > 0) stream.add(docx.pageBreak());
        genreToc("Science Fantasy", scienceFantasy, renderSection, docx, stream);

        //stream.add(docx.pageBreak());

        // Bonus Original
        stream.add(docx.pageBreak());
        stream.add(
                docx.drawings(
                        docxImage.textImages("The Bonus Collection", FontSize.of(180), docx)));

        var originalScienceFiction = stories(htmlManuscript, isOriginal, Section.Genre.ScienceFiction);
        var originalFantasy = stories(htmlManuscript, isOriginal, Section.Genre.Fantasy);
        var originalScienceFantasy = stories(htmlManuscript, isOriginal, Section.Genre.ScienceFantasy);

        genreToc("Science Fiction", originalScienceFiction, renderSection, docx, stream);
        genreToc("Fantasy", originalFantasy, renderSection, docx, stream);
        genreToc("Science Fantasy", originalScienceFantasy, renderSection, docx, stream);

        return stream.build();
    }

    @Override
    public Stream<HtmlSection> stories(HtmlManuscript htmlManuscript) {
        return tocSections(htmlManuscript, HtmlSection::isDocx)
                .filter(HtmlSection::isStory);
    }

    private void genreToc(
            String title,
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
                        docxImage.textImages(title, FontSize.of(120), docx)));
        // items
        sections.stream()
                .flatMap(renderSection)
                .forEach(outputStream::add);
    }

}
