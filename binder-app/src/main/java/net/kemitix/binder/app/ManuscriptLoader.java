package net.kemitix.binder.app;

import org.yaml.snakeyaml.error.YAMLException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApplicationScoped
public class ManuscriptLoader {

    private final SectionLoader sectionLoader;
    private final YamlLoader yamlLoader;

    @Inject
    public ManuscriptLoader(
            SectionLoader sectionLoader,
            YamlLoader yamlLoader
    ) {
        this.sectionLoader = sectionLoader;
        this.yamlLoader = yamlLoader;
    }

    @Produces
    ManuscriptMetadata manuscriptMetadata(BinderConfig binderConfig) {
        File scanDirectory = binderConfig.getScanDirectory();
        if (!scanDirectory.exists()) {
            throw new MissingBinderDirectory(scanDirectory);
        }
        File file = scanDirectory.toPath().resolve("binder.yaml").toFile();
        ManuscriptMetadata manuscriptMetadata =
                yamlLoader.loadFile(file, ManuscriptMetadata.class);
        if (manuscriptMetadata.getContents() == null)
            manuscriptMetadata.setContents(Collections.emptyList());
        return manuscriptMetadata;
    }

    @Produces
    Manuscript manuscript(ManuscriptMetadata metadata) {
        Manuscript manuscript = new Manuscript();
        manuscript.setMetadata(metadata);
        manuscript.setContents(loadSections(metadata.getSections()));
        return manuscript;
    }

    private List<Section> loadSections(
            List<String> filenames
    ) {
        if (filenames == null) return Collections.emptyList();
        return filenames.stream()
                .map(this::loadFile)
                .collect(Collectors.toList());
    }

    private Section loadFile(
            String filename
    ) {
        return sectionLoader.load(filename);
    }

}
