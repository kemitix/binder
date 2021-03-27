package net.kemitix.binder.docx;

import lombok.Getter;
import net.kemitix.binder.spi.Section;

import java.util.Collection;
import java.util.function.Function;

public class DocxContent {

    @Getter
    private final Collection<?> contents;
    @Getter
    private final Section.Name sectionName;

    public DocxContent(
            Section.Name sectionName,
            Collection<?> contents
    ) {
        this.sectionName = sectionName;
        this.contents = contents;
    }

}
