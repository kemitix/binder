package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.markdown.Context;
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
        AggregateRenderer<DocxTocItemRenderer, Section, Object> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<DocxTocItemRenderer> tocItemRenderers;
    private final DocxFacade docx;
    private final DocxImageFacade docxImage;

    @Inject
    public TocDocxRenderer(
            HtmlManuscript htmlManuscript,
            Instance<DocxTocItemRenderer> tocItemRenderers,
            DocxFacade docx,
            DocxImageFacade docxImage
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
        this.docx = docx;
        this.docxImage = docxImage;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.toc);
    }

    @Override
    public Stream<DocxContent> render(Section section) {
        //TODO: use Arrays.asList(...)
        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(
                docx.drawings(
                        docxImage.textImages(
                                "Contents",
                                FontSize.of(240))));
        contents.add(docx.textParagraph(""));
        htmlManuscript.sections()
                .filter(Section::isDocx)
                .filter(Section::isToc)
                .flatMap(s ->
                        findRenderer(s, tocItemRenderers)
                                .render(s))
                .forEach(contents::add);
        Context context = Context.create(section);
        return Stream.of(new DocxContent(docx.finaliseTitlePage(context, contents)));
    }

}
