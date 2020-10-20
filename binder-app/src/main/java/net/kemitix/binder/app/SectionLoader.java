package net.kemitix.binder.app;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

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
        return loadSection(filename)
                .withFilename(filename)
                .withName(name)
                ;
    }

    private Section loadSection(File filename) {
        try {
            return yamlLoader.loadSectionFile(filename);
        } catch (IOException e) {
            throw new ManuscriptFormatException(String.format(
                    "Error loading section: %s", filename), e);
        }
    }
}
