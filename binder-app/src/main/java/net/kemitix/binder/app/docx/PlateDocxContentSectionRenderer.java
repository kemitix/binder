package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.HtmlSection;
import net.kemitix.binder.app.SectionRenderer;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;

@ApplicationScoped
public class PlateDocxContentSectionRenderer
        implements SectionRenderer<HtmlSection, DocxContent> {

    @Override
    public boolean canHandle(String type) {
        return "plate".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        //TODO: implement properly
        return new DocxContent(new ArrayList<>());
    }
}
