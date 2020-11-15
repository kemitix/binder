package net.kemitix.binder.docx;

import lombok.Getter;

import java.util.Collection;

public class DocxContent {

    @Getter
    private Collection<?> contents;

    public DocxContent(Collection<?> contents) {
        this.contents = contents;
    }

}
