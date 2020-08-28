package net.kemitix.binder.app;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@ApplicationScoped
public class ManuscriptLoader {

    private final SectionLoader sectionLoader;

    @Inject
    public ManuscriptLoader(SectionLoader sectionLoader) {
        this.sectionLoader = sectionLoader;
    }

    @Produces
    ManuscriptMetadata manuscriptMetadata(BinderConfig binderConfig) throws IOException {
        File scanDirectory = binderConfig.getScanDirectory();
        if (!scanDirectory.exists()) {
            throw new MissingBinderDirectory(scanDirectory);
        }
        Yaml yaml = new Yaml(new Constructor(ManuscriptMetadata.class));
        File file = scanDirectory.toPath().resolve("binder.yaml").toFile();
        if (!file.exists()) {
            throw new MissingBinderYamlFile(file);
        }
        FileReader fileReader = new FileReader(file);
        return yaml.load(fileReader);
    }

    @Produces
    Manuscript manuscript(ManuscriptMetadata metadata) {
        Manuscript manuscript = new Manuscript();
        manuscript.setMetadata(metadata);
        loadSections(metadata.getPreludes(), manuscript::getPreludes);
        loadSections(metadata.getContents(), manuscript::getContents);
        loadSections(metadata.getCodas(), manuscript::getCodas);
        return manuscript;
    }

    private void loadSections(
            List<String> filenames,
            Supplier<List<Manuscript.Section>> sections
    ) {
        if (filenames == null) return;
        filenames.forEach(filename -> loadFile(sections, filename));
    }

    private void loadFile(
            Supplier<List<Manuscript.Section>> sections,
            String filename
    ) {
        sections.get()
                .add(sectionLoader.load(filename));
    }

}
