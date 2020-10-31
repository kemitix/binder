package net.kemitix.binder.docx;

import net.kemitix.binder.spi.HtmlSection;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StoryDocxTocItemRenderer
        implements DocxTocItemRenderer {

    private final DocxFacade docx;

    @Inject
    public StoryDocxTocItemRenderer(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(String type) {
        return "story".equals(type);
    }

    @Override
    public Object render(Section source) {
        return docx.tocItem(Integer.toString(source.getPage()), source.getTitle());
    }
}
