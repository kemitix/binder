package net.kemitix.binder.app.docx;

import net.kemitix.binder.app.Section;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;

@ApplicationScoped
public class DocxContentFactory {
    public DocxContent create(Section section) {
        //TODO: create DocxContent from Section
        File htmlFile = section.getHtmlFile();
        return new DocxContent();
    }
}
