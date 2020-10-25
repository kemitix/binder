package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

@Log
@ApplicationScoped
public class PlateDocxRenderer
        implements DocxRenderer {

    private final DocxFacade docx;

    @Inject
    public PlateDocxRenderer(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(String type) {
        return "plate".equals(type);
    }

    @Override
    public DocxContent render(HtmlSection htmlSection) {
        log.info("PLATE: %s".formatted(htmlSection.getName()));
        ArrayList<Object> contents = new ArrayList<>();
        contents.add(docx.textImage(htmlSection.getMarkdown(), FontSize.of(512)));
        contents.add(docx.breakToOddPage());
        return new DocxContent(contents);
    }

}
