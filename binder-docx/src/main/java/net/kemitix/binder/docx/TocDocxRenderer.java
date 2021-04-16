package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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
        return section.isType(Section.Type.toc);
    }

    @Override
    public Stream<DocxContent> render(Section section, Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        //TODO: use Arrays.asList(...)
        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                "Contents",
                                FontSize.of(240), docx)));
        contents.add(docx.textParagraph(""));
        htmlManuscript.sections()
                .filter(Section::isDocx)
                .filter(Section::isToc)
                .flatMap(s ->
                        findRenderer(s, tocItemRenderers)
                                .render(s, context))
                .forEach(contents::add);
        return Stream.of(
                new DocxContent(
                        section.getName(),
                        docx.finaliseTitlePage(context, contents))
        );
    }

}
