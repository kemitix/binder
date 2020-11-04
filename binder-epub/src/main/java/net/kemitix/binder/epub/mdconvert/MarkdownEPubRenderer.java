package net.kemitix.binder.epub.mdconvert;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.epub.EpubRenderer;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@EPub
@ApplicationScoped
public class MarkdownEPubRenderer
        implements EpubRenderer {

    private final MarkdownConverter converter;

    @Inject
    public MarkdownEPubRenderer(
            @EPub MarkdownConverter converter
    ) {
        this.converter = converter;
    }

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public Content render(HtmlSection source) {
        Object[] converted = converter.convert(source.getMarkdown());
        return ((Content) converted[0]);
    }


}
