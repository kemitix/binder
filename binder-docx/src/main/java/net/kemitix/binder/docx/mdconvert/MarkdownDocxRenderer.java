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
    public boolean canHandle(Section.Type type) {
        return Section.Type.html.equals(type)
                || Section.Type.story.equals(type);
    }

    @Override
    public Stream<DocxContent> render(Section source) {
        List<Object> contents = new ArrayList<>();
        if (Section.Type.story.equals(source.getType())) {
            contents.addAll(docx.leaders());
        }
        addTitle(source, contents);
        if (Section.Type.story.equals(source.getType())) {
            contents.add(docx.textParagraphCentered(source.getAuthor()));
            contents.addAll(docx.leaders());
        }

        Stream<Object> objects = converter.convert(source);

        contents.addAll(objects.collect(Collectors.toList()));

        if (Section.Type.story.equals(source.getType())) {
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
