package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.HtmlSection;

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
        int pageWidth = 0;// TODO
        contents.add(docxHelper.textImage(htmlSection.getMarkdown(), 512, pageWidth));
        contents.add(docxHelper.breakToOddPage());
        return new DocxContent(contents);
    }

}
