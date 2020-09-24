package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;
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
    @ApplicationScoped
    Metadata metadata(BinderConfig binderConfig) {
        File scanDirectory = binderConfig.getScanDirectory();
        if (!scanDirectory.exists()) {
            throw new MissingBinderDirectory(scanDirectory);
        }
        File file = scanDirectory.toPath().resolve("binder.yaml").toFile();
        Metadata metadata =
                yamlLoader.loadFile(file, Metadata.class);
        if (metadata.getContents() == null)
            metadata.setContents(Collections.emptyList());
        return metadata;
    }

    @Produces
    @ApplicationScoped
    Manuscript manuscript(Metadata metadata) {
        return Manuscript.builder()
                .metadata(metadata)
                .contents(loadSections(metadata.getContents()));
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
