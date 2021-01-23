package net.kemitix.binder.app;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptFormatException;
import net.kemitix.binder.spi.Section;
import org.yaml.snakeyaml.error.YAMLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

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
        return yamlLoader.loadSectionFile(filename);
    }
}
