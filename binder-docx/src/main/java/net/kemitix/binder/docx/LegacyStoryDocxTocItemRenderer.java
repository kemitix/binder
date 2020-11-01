package net.kemitix.binder.docx;

import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LegacyStoryDocxTocItemRenderer
        implements LegacyDocxTocItemRenderer {

    private final DocxFacade docx;

    @Inject
    public LegacyStoryDocxTocItemRenderer(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(String type) {
        return "story".equals(type);
    }

    @Override
    public Object render(HtmlSection source) {
        return docx.tocItem(Integer.toString(source.getPage()), source.getTitle());
    }
}
