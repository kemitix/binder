package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.HtmlSection;

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
