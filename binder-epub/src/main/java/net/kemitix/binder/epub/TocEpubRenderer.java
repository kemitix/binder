package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.markdown.DocumentNodeHandler;
import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Epub
@ApplicationScoped
public class TocEpubRenderer
        implements EpubRenderer,
        AggregateRenderer<EpubTocItemRenderer, HtmlSection, String, EpubRenderHolder> {

    private final HtmlManuscript htmlManuscript;
    private final Instance<EpubTocItemRenderer> tocItemRenderers;
    private final DocumentNodeHandler<String, EpubRenderHolder> documentNodeHandler;

    @Inject
    public TocEpubRenderer(
            HtmlManuscript htmlManuscript,
            Instance<EpubTocItemRenderer> tocItemRenderers,
            @Epub DocumentNodeHandler<String, EpubRenderHolder> documentNodeHandler
    ) {
        this.htmlManuscript = htmlManuscript;
        this.tocItemRenderers = tocItemRenderers;
        this.documentNodeHandler = documentNodeHandler;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.toc);
    }

    @Override
    public Stream<Content> render(
            HtmlSection htmlSection,
            Context<EpubRenderHolder> epubRenderHolder
    ) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<ul>");
        htmlManuscript.sections()
                .filter(HtmlSection::isEpub)
                .filter(HtmlSection::isToc)
                .flatMap(section ->
                        findRenderer(section, tocItemRenderers)
                                .render(section, epubRenderHolder))
                .forEach(htmlBuilder::append);
        htmlBuilder.append("</ul>");
        String body = htmlBuilder.toString();
        String href = htmlSection.getHref();
        String html = documentNodeHandler
                .documentBody(
                        htmlSection.getTitle(),
                        Stream.of(body))
                .collect(Collectors.joining());
        return Stream.of(
                new Content(href, html.getBytes(StandardCharsets.UTF_8))
        );
    }

}
