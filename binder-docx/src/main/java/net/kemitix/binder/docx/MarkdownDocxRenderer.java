package net.kemitix.binder.docx;

import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.docx.mdconvert.MarkdownDocxConverter;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Docx
@ApplicationScoped
public class MarkdownDocxRenderer
        implements DocxRenderer {

    private final DocxFacade docx;
    private final DocxImageFacade docxImage;
    private final MarkdownConverter converter;

    @Inject
    public MarkdownDocxRenderer(
            DocxFacade docx,
            DocxImageFacade docxImage,
            @Docx MarkdownConverter converter
    ) {
        this.docx = docx;
        this.docxImage = docxImage;
        this.converter = converter;
    }


    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public DocxContent render(Section source) {
        List<Object> contents = new ArrayList<>();
        if ("story".equals(source.getType())) {
            contents.addAll(docx.leaders());
        }
        addTitle(source, contents);
        if ("story".equals(source.getType())) {
            contents.add(docx.textParagraphCentered(source.getAuthor()));
            contents.addAll(docx.leaders());
        }

        Object[] objects = converter.convert(source.getMarkdown());

        contents.addAll(Arrays.asList(objects));
        contents.add(docx.breakToOddPage());
        return new DocxContent(contents);
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
