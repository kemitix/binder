package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

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
        implements DocxRenderer,
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
        Predicate<HtmlSection> isStory = htmlSection -> htmlSection.isType(Section.Type.story);

        var originals = docxTocStream().filter(isStory).filter(isOriginal).collect(Collectors.toList());
        var reprints = docxTocStream().filter(isStory).filter(isReprint).collect(Collectors.toList());

        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                section.getTitle(),// "Contents"
                                FontSize.of(240), docx)));
        contents.add(docx.textParagraph(""));

        log.info("Originals: " + originals.size());
        log.info("Reprints : " + reprints.size());

        if (originals.size() > 0 && reprints.size() == 0) {
            singleIssueToc(docxTocStream(), renderSection, docx)
                    .forEachOrdered(contents::add);
        } else if (reprints.size() > 0) {
            yearsCollectionToc(docxTocStream(), renderSection, docx)
                    .forEachOrdered(contents::add);
        }

        return Stream.of(
                new DocxContent(
                        section.getName(),
                        docx.finaliseTitlePage(context, contents))
        );
    }

    private Stream<Object> singleIssueToc(
            Stream<HtmlSection> sections,
            Function<HtmlSection, Stream<?>> renderSection,
            DocxFacade docx
    ) {
        var stream = Stream.builder();

        var scienceFiction = docxTocStream().filter(isGenre(Section.Genre.ScienceFiction)).collect(Collectors.toList());
        var fantasy = docxTocStream().filter(isGenre(Section.Genre.Fantasy)).collect(Collectors.toList());
        var scienceFantasy = docxTocStream().filter(isGenre(Section.Genre.ScienceFantasy)).collect(Collectors.toList());

        genreToc("Science Fiction", scienceFiction, renderSection, docx, stream);
        if (scienceFiction.size() > 0 && fantasy.size() > 0) stream.add(docx.pageBreak());
        genreToc("Fantasy", fantasy, renderSection, docx, stream);
        if (fantasy.size() > 0 && scienceFantasy.size() > 0) stream.add(docx.pageBreak());
        genreToc("Science Fantasy", scienceFantasy, renderSection, docx, stream);

        return stream.build();
    }

    private Stream<Object> yearsCollectionToc(
            Stream<HtmlSection> sections,
            Function<HtmlSection, Stream<?>> renderSection,
            DocxFacade docx
    ) {
        log.info("YearsCollectionToc");
        var stream = Stream.builder();

        Predicate<HtmlSection> isOriginal = HtmlSection::isOriginal;
        Predicate<HtmlSection> isReprint = isOriginal.negate();

        // Years Collection
        stream.add(
                docx.drawings(
                        docxImage.textImages("Year's Collection", FontSize.of(180), docx)));

        var scienceFiction = docxTocStream().filter(isGenre(Section.Genre.ScienceFiction)).filter(isReprint).collect(Collectors.toList());
        var fantasy = docxTocStream().filter(isGenre(Section.Genre.Fantasy)).filter(isReprint).collect(Collectors.toList());
        var scienceFantasy = docxTocStream().filter(isGenre(Section.Genre.ScienceFantasy)).filter(isReprint).collect(Collectors.toList());

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
                        docxImage.textImages("Bonus Original", FontSize.of(180), docx)));

        var originalScienceFiction = docxTocStream().filter(isGenre(Section.Genre.ScienceFiction)).filter(isOriginal).collect(Collectors.toList());
        var originalFantasy = docxTocStream().filter(isGenre(Section.Genre.Fantasy)).filter(isOriginal).collect(Collectors.toList());
        var originalScienceFantasy = docxTocStream().filter(isGenre(Section.Genre.ScienceFantasy)).filter(isOriginal).collect(Collectors.toList());

        genreToc("Science Fiction", originalScienceFiction, renderSection, docx, stream);
        genreToc("Fantasy", originalFantasy, renderSection, docx, stream);
        genreToc("Science Fantasy", originalScienceFantasy, renderSection, docx, stream);

        return stream.build();
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

    private Predicate<HtmlSection> isGenre(Section.Genre genre) {
        return htmlSection -> htmlSection.isGenre(genre);
    }

    private Stream<HtmlSection> docxTocStream() {
        return htmlManuscript.sections().filter(Section::isDocx).filter(Section::isToc);
    }

}
