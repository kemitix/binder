package net.kemitix.binder.docx;

import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StoryDocxTocItemRenderer
        implements DocxTocItemRenderer {

    private final DocxHelper docxHelper;

    @Inject
    public StoryDocxTocItemRenderer(DocxHelper docxHelper) {
        this.docxHelper = docxHelper;
    }

    @Override
    public boolean canHandle(String type) {
        return "story".equals(type);
    }

    @Override
    public Object render(HtmlSection source) {
        return docxHelper.tocItem(Integer.toString(source.getPage()), source.getTitle());
    }
}
