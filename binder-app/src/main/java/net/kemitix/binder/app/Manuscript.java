package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Manuscript {

    ManuscriptMetadata metadata;

    private List<Section> prelude = new ArrayList<>();
    private List<Section> contents = new ArrayList<>();
    private List<Section> coda = new ArrayList<>();

    @Setter
    @Getter
    public static class Section {

        private String name; // the base filename
        private File filename; // the file loaded
        private String markdown; // the contents of the file

    }

    @Setter
    @Getter
    public static class ContentSection extends Section {

        private String title;
        private String author;
        private int page;

    }

}
