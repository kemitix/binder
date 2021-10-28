package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import net.kemitix.mon.TypeAlias;

import java.io.File;
import java.util.Date;

/**
 * A general section of the document. Sourced from a single Markdown file.
 *
 * <p>All preludes, contents and codas are sections.</p>
 */
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Section {

    @Getter
    private Type type;
    @Getter
    private boolean toc = true; // show in table of contents
    @Getter
    private boolean original = false; // show in toc-originals
    @Getter
    private boolean template = false; // should expand template variables
    @Getter
    private boolean epub = true; // include in epub output
    @Getter
    private boolean docx = true; // include in docx output
    @Getter
    private String title; // the title, taken from the yaml header
    private String name; // the base filename
    @Getter
    private String author;
    @Getter
    private String bio = "TODO"; // the author bio
    @Getter
    private float fontSize = 11;
    @Getter
    private boolean header = true; // should there be a header
    @Getter
    private boolean footer = true; // should there be a footer
    @Getter
    private int page = 0; // page in paperback to toc
    @Getter
    private Date date; // when/if should be published to website
    @Getter
    private int copyright; // the year the story was copyrighted
    @Getter
    private File filename; // the file loaded
    @Getter
    private String markdown; // the markdown contents of the file, after removing yaml header
    @Getter
    private Align align = Align.full; // full justify paragraphs
    @Getter
    private boolean landmark = false; // is an epub landmark
    @Getter
    private LandmarkType landmarkType;
    @Getter
    private boolean last; // internal flag to mark last section in document
    private boolean startOnOddPage = true; // start each new section on an odd numbered page
    private Genre genre;

    public boolean isType(Type type) {
        return this.type.equals(type);
    }

    public static Section.Name name(String name) {
        return new Section.Name(name);
    }

    public Section.Name getName() {
        return Section.name(name);
    }

    public boolean startOnOddPage() {
        return startOnOddPage;
    }

    public boolean isGenre(Genre genre) {
        return this.genre == genre;
    }

    public boolean isStory() {
        return this.isType(Type.story);
    }

    public enum Type {

        plate,
        title,
        toc,
        tocoriginals,
        markdown,
        story,
        ;

        public boolean isA(Section section) {
            return section.type.equals(this);
        }

    }

    public enum Align {
        left,
        right,
        full,
        centre
    }

    public enum LandmarkType {
        cover("cover"),
        toc("toc"),
        bodymatter("bodymatter"),
        titlepage("titlepage"),
        frontmatter("frontmatter"),
        backmatter("backmatter"),
        loi("loi"),
        lot("lot"),
        preface("preface"),
        bibliography("bibliography"),
        index("index"),
        glossary("glossary"),
        acknowledgments("acknowledgments"),
        ;

        @Getter
        private final String value;

        @Override
        public String toString() {
            return value;
        }

        LandmarkType(String type) {
            this.value = type;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Genre {
        ScienceFiction("Science Fiction"),
        Fantasy("Fantasy"),
        ScienceFantasy("Science Fantasy"),
        ;

        private final String value;

        @Override
        public String toString() {
            return value;
        }
    }

    public static class Name extends TypeAlias<String> {
        private Name(String name) {
            super(name);
        }
    }
}
