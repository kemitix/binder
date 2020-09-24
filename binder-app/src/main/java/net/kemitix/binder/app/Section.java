package net.kemitix.binder.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.io.File;

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

    private String type; // prelude, content or coda
    private boolean toc = true; // show in table of contents
    private String title; // the title, taken from the yaml header
    private String name; // the base filename
    private File filename; // the file loaded
    private String markdown; // the markdown contents of the file, after removing yaml header
    private File htmlFile;

}
