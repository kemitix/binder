package net.kemitix.binder.docx;

import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class DefaultDocxTocItemRenderer
        implements DocxTocItemRenderer {

    private final DocxHelper docxHelper;

    @Inject
    public DefaultDocxTocItemRenderer(DocxHelper docxHelper) {
        this.docxHelper = docxHelper;
    }

    @Override
    public boolean canHandle(String type) {
        // anything except story
        return !"story".equals(type);
    }

    @Override
    public Object render(HtmlSection source) {
        return docxHelper.tocItem("", source.getTitle());
    }
}
