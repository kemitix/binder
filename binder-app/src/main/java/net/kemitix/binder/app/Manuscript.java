package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class Manuscript {

    ManuscriptMetadata metadata;

    private List<Section> preludes = new ArrayList<>();
    private List<Section> contents = new ArrayList<>();
    private List<Section> codas = new ArrayList<>();

    @Setter
    @Getter
    @ToString
    public static class Section {

        private String name; // the base filename
        private File filename; // the file loaded
        private String markdown; // the contents of the file

    }

    @Setter
    @Getter
    @ToString
    public static class ContentSection extends Section {

        private String title;
        private String author;
        private int page;

    }

}
