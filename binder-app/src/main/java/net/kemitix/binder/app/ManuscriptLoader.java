package net.kemitix.binder.app;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    public Metadata metadata(BinderConfig binderConfig) {
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
    public MdManuscript mdManuscript(Metadata metadata) {
        return MdManuscript.builder()
                .metadata(metadata)
                .contents(loadSections(metadata.getContents()));
    }

    private List<Section> loadSections(
            List<String> filenames
    ) {
        if (filenames == null) return Collections.emptyList();
        return filenames.stream()
                .map(sectionLoader::load)
                .collect(Collectors.toList());
    }

    @Produces
    @ApplicationScoped
    public HtmlManuscript htmlManuscript(
            MdManuscript mdManuscript,
            MarkdownToHtml markdownToHtml
    ) {
        Map<String, String> htmlSections = new LinkedHashMap<>();
        mdManuscript.getContents()
                .forEach(section ->
                        htmlSections.put(
                                section.getName(),
                                markdownToHtml.apply(section)));
        return HtmlManuscript.htmlBuilder()
                .metadata(mdManuscript)
                .htmlSections(htmlSections);
    }

}
