package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

@Log
@ApplicationScoped
public class PlateDocxRenderer
        implements DocxRenderer {

    private final DocxHelper docxHelper;

    @Inject
    public PlateDocxRenderer(DocxHelper docxHelper) {
        this.docxHelper = docxHelper;
    }

    @Override
    public boolean canHandle(String type) {
        return "plate".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("PLATE: %s".formatted(htmlSection.getName()));
        ArrayList<Object> contents = new ArrayList<>();
        contents.add(docxHelper.textImage(htmlSection.getMarkdown(), 480));
        contents.add(docxHelper.breakToOddPage());
        return new DocxContent(contents);
    }

}
