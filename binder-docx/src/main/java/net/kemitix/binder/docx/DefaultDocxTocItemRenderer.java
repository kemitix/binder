package net.kemitix.binder.docx;

import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DefaultDocxTocItemRenderer
        implements DocxTocItemRenderer {

    private final DocxFacade docx;

    @Inject
    public DefaultDocxTocItemRenderer(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(String type) {
        // anything except story
        return !"story".equals(type);
    }

    @Override
    public Object render(HtmlSection source) {
        return docx.tocItem("", source.getTitle());
    }
}
