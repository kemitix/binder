package net.kemitix.binder.app.docx;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

public class DocxContent {

    @Getter
    private Collection<?> contents = new ArrayList<>();

    public DocxContent(Collection<?> contents) {
        this.contents = contents;
    }

}
