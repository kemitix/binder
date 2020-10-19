package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.HtmlSection;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

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
