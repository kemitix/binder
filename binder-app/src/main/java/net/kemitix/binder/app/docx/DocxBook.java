package net.kemitix.binder.app.docx;

import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@Log
public class DocxBook {

    private final String language;
    private final String id;
    private final String title;
    private final String editor;
    private final List<DocxContent> content = new ArrayList<>();

    public DocxBook(
            String language,
            String id,
            String title,
            String editor
    ) {
        this.language = language;
        this.id = id;
        this.title = title;
        this.editor = editor;
    }

    public void writeToFile(String file) {
        //TODO write self to file
        log.info("TODO - write " + title + " to: " + file);
        content.forEach(docxContent -> docxContent.save(file));
    }

    public void addContent(DocxContent content) {
        this.content.add(content);
    }

}
