package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

/**
 * A general section of the document. Sourced from a single Markdown file.
 *
 * <p>All preludes, contents and codas are sections.</p>
 */
@Setter
@Getter
@ToString
public class Section {

    private String type; // prelude, content or coda
    private String title; // the title, taken from the yaml header
    private String name; // the base filename
    private File filename; // the file loaded
    private String markdown; // the markdown contents of the file, after removing yaml header
    private String html;
    private File htmlFile;

    @Getter
    public enum Type {
        PRELUDE("prelude", Prelude.class),
        CONTENT("content", Content.class),
        CODA("coda", Coda.class)
        ;

        private final String slug;
        private final Class<? extends Section> aClass;

        Type(
                String slug,
                Class<? extends Section> aClass
        ) {
            this.slug = slug;
            this.aClass = aClass;
        }
    }

}
