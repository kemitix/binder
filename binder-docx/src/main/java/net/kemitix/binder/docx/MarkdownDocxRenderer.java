package net.kemitix.binder.docx;

import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.spi.Context;
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

    private final DocxImageFacade docxImage;
    private final MarkdownConverter<Object, DocxRenderHolder> converter;

    @Inject
    public MarkdownDocxRenderer(
            DocxImageFacade docxImage,
            @Docx MarkdownConverter<Object, DocxRenderHolder> converter
    ) {
        this.docxImage = docxImage;
        this.converter = converter;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.markdown);
    }

    @Override
    public Stream<DocxContent> render(
            Section section,
            Context<DocxRenderHolder> context
    ) {
        var docx = context.getRendererHolder().getRenderer();
        List<Object> contents = new ArrayList<>();
        addTitle(docx, section, contents);
        List<Object> sectionPs =
                converter.convert(
                        context,
                        section.getMarkdown()
                ).collect(Collectors.toList());
        contents.addAll(sectionPs);
        docx.addStyle(docx.paraStyle(context));
        return Stream.of(
                new DocxContent(
                        section.getName(),
                        docx.finaliseTitlePage(context, contents))
        );
    }

    private void addTitle(
            DocxFacade docx,
            Section sec,
            List<Object> contents
    ) {
        String title = getTitle(sec);
        if (title.length() > 0) {
            contents.add(docx.textParagraph(""));
            contents.add(
                    docx.drawings(
                            docxImage.textImages(
                                    title,
                                    FontSize.of(240), docx)));
            contents.add(docx.textParagraph(""));
        }
    }

    private String getTitle(Section section) {
        return Objects.requireNonNullElse(section.getTitle(), "");
    }
}
