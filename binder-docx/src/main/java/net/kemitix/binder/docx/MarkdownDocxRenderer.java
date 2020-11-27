package net.kemitix.binder.docx;

import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;
import org.docx4j.wml.P;
import org.docx4j.wml.Style;

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
        return section.isType(Section.Type.markdown);
    }

    @Override
    public Stream<DocxContent> render(Section section) {
        List<Object> contents = new ArrayList<>();
        addTitle(section, contents);
        Context context = Context.create(section);
        List<Object> sectionPs =
                converter.convert(
                        context,
                        section.getMarkdown()
                ).collect(Collectors.toList());
        contents.addAll(docx.finaliseTitlePage(context, sectionPs));
        docx.addStyle(docx.paraStyle(context));
        return Stream.of(new DocxContent(contents));
    }

    private Style charStyle(Section section) {
        String styleName = "section-%s-char".formatted(section.getName());
        return docx.createCharStyleBasedOn(styleName, "D");
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
