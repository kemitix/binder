package net.kemitix.binder.app.docx;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import javax.enterprise.inject.Vetoed;
import java.util.ArrayList;
import java.util.List;

@Log
@Vetoed
@NoArgsConstructor
@AllArgsConstructor
public class DocxBook {

    private String language;
    private String id;
    private String title;
    private String editor;
    private final List<DocxContent> content = new ArrayList<>();

    public void writeToFile(String file) {
        //TODO write self to file
        log.info("TODO - write " + title + " to: " + file);
        content.forEach(docxContent -> docxContent.save(file));
    }

    public void addContent(DocxContent content) {
        this.content.add(content);
    }

}
