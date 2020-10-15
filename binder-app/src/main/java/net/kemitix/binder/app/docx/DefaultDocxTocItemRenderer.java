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

    private final ObjectFactory factory;

    @Inject
    public DefaultDocxTocItemRenderer(ObjectFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean canHandle(String type) {
        // anything except story
        return !"story".equals(type);
    }

    @Override
    public Object render(HtmlSection source) {
        Text page = factory.createText();
        page.setValue("(tab) ");
        R pageR = factory.createR();
        pageR.getContent().add(page);
        Text title = factory.createText();
        title.setValue(source.getTitle());
        R titleR = factory.createR();
        titleR.getContent().add(title);
        P item = factory.createP();
        item.getContent().add(pageR);
        item.getContent().add(titleR);
        return item;
    }
}
