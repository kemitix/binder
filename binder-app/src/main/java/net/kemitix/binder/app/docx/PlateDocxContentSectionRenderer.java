package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;

@Log
@ApplicationScoped
public class PlateDocxContentSectionRenderer
        implements DocxSectionRenderer {

    @Override
    public boolean canHandle(String type) {
        return "plate".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("PLATE: %s".formatted(htmlSection.getName()));
        //TODO: implement properly
        return new DocxContent(new ArrayList<>());
    }
}
