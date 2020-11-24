package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.io.File;
import java.util.Date;

/**
 * A general section of the document. Sourced from a single Markdown file.
 *
 * <p>All preludes, contents and codas are sections.</p>
 */
@Setter
@Getter
@With
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Section {

    private Type type;
    private boolean toc = true; // show in table of contents
    private boolean template = false; // should expand template variables
    private boolean epub = true; // include in epub output
    private boolean docx = true; // include in docx output
    private String title; // the title, taken from the yaml header
    private String name; // the base filename
    private String author;
    private String bio = "TODO"; // the author bio
    private int page = 0; // page in paperback to toc
    private Date date; // when/if should be published to website
    private int copyright; // the year the story was copyrighted
    private File filename; // the file loaded
    private String markdown; // the markdown contents of the file, after removing yaml header
    private Align align = Align.full; // full justify paragraphs

    public boolean isType(Type type) {
        return this.type.equals(type);
    }

    public enum Type {

        plate,
        title,
        toc,
        markdown,
        story,
        ;

        public boolean isA(Section section) {
            return section.getType().equals(this);
        }

    }

    public enum Align {
        left,
        right,
        full,
        centre
    }
}
