package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@ApplicationScoped
public class SectionLoader {

    private final BinderConfig binderConfig;
    private final YamlLoader yamlLoader;

    @Inject
    public SectionLoader(
            BinderConfig binderConfig,
            YamlLoader yamlLoader
    ) {
        this.binderConfig = binderConfig;
        this.yamlLoader = yamlLoader;
    }

    public Section load(String name) {
        File filename = binderConfig.getFile(name, "md");
        Section section = getSection(filename);
        return Arrays.stream(Section.Type.values())
                .filter(t1 -> section.getType().equals(t1.getSlug()))
                .map(type -> getSection(filename, type))
                .peek(s -> s.setName(name))
                .peek(s -> s.setFilename(filename))
                .findFirst()
                .orElseThrow(() ->
                        new ManuscriptFormatException(
                                "Unknown section type %s: %s"
                                        .formatted(
                                                section.getType(),
                                                filename)));
    }

    private Section getSection(File filename, Section.Type type) {
        try {
            return yamlLoader.loadSectionFile(filename, type.getAClass());
        } catch (IOException e) {
            throw new ManuscriptFormatException(String.format(
                    "Error loading %s section: %s",
                    type.getSlug(), filename), e);
        }
    }

    private Section getSection(File filename) {
        try {
            return yamlLoader.loadSectionFile(filename, Section.class);
        } catch (IOException e) {
            throw new ManuscriptFormatException(String.format(
                    "Error loading Basic section: %s",
                    filename), e);
        }
    }
}
