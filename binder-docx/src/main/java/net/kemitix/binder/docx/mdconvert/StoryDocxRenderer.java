package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxImageFacade;
import net.kemitix.binder.docx.DocxRenderer;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class StoryDocxRenderer
        implements DocxRenderer {

    private final DocxFacade docx;
    private final DocxImageFacade docxImage;
    private final MarkdownConverter<Object> converter;

    @Inject
    public StoryDocxRenderer(
            DocxFacade docx,
            DocxImageFacade docxImage,
            @Docx MarkdownConverter<Object> converter
    ) {
        this.docx = docx;
        this.docxImage = docxImage;
        this.converter = converter;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<DocxContent> render(Section section) {
        List<Object> contents = new ArrayList<>();
        contents.addAll(docx.leaders());
        addTitle(section, contents);
        contents.add(docx.textParagraphCentered(section.getAuthor()));
        contents.addAll(docx.leaders());

        Stream<Object> objects =
                converter.convert(
                        Context.create(section),
                        section.getMarkdown()
                );

        contents.addAll(objects.collect(Collectors.toList()));

        contents.addAll(aboutAuthor(section));

        contents.add(docx.breakToOddPage());
        return Stream.of(new DocxContent(contents));
    }

    private Collection<?> aboutAuthor(Section section) {
        return Arrays.asList(
                docx.keepWithNext(docx.p()),
                docx.keepWithNext(docx.textParagraphCentered(
                        "© %4d %s".formatted(
                                section.getCopyright(), section.getAuthor()
                        ))),
                docx.keepWithNext(docx.p()),
                // TODO: history - if present
                docx.keepWithNext(docx.textParagraphCentered("About the Author")),
                docx.keepTogether(docx.textParagraph(section.getBio()))
        );
    }

    private void addTitle(Section sec, List<Object> contents) {
        String title = getTitle(sec);
        if (title.length() > 0) {
            contents.add(docx.textParagraph(""));
            contents.add(
                    docx.drawings(
                            docxImage.textImages(
                                    title,
                                    FontSize.of(240))));
            contents.add(docx.textParagraph(""));
        }
    }

    private String getTitle(Section section) {
        return Objects.requireNonNullElse(section.getTitle(), "");
    }
}
