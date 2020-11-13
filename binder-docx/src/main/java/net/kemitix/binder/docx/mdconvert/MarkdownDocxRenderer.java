package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxImageFacade;
import net.kemitix.binder.docx.DocxRenderer;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class MarkdownDocxRenderer
        implements DocxRenderer {

    private final DocxFacade docx;
    private final DocxImageFacade docxImage;
    private final MarkdownConverter<Object> converter;

    @Inject
    public MarkdownDocxRenderer(
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
        return section.isType(Section.Type.markdown)
                || section.isType(Section.Type.story);
    }

    @Override
    public Stream<DocxContent> render(Section source) {
        List<Object> contents = new ArrayList<>();
        if (source.isType(Section.Type.story)) {
            contents.addAll(docx.leaders());
        }
        addTitle(source, contents);
        if (source.isType(Section.Type.story)) {
            contents.add(docx.textParagraphCentered(source.getAuthor()));
            contents.addAll(docx.leaders());
        }

        Stream<Object> objects = converter.convert(source);

        contents.addAll(objects.collect(Collectors.toList()));

        if (source.isType(Section.Type.story)) {
            //TODO add previously published section if required
            //TODO add about the Author sections
        }

        contents.add(docx.breakToOddPage());
        return Stream.of(new DocxContent(contents));
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
