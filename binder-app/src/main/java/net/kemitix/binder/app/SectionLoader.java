package net.kemitix.binder.app;

import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Files;

@ApplicationScoped
public class SectionLoader {

    private final BinderConfig binderConfig;

    @Inject
    public SectionLoader(BinderConfig binderConfig) {
        this.binderConfig = binderConfig;
    }

    @SneakyThrows
    public Manuscript.Section load(String name) {
        Manuscript.Section section = new Manuscript.Section();
        section.setName(name);
        section.setFilename(binderConfig.getFile(name, "md"));
        section.setMarkdown(Files.readString(section.getFilename().toPath()));
        return section;
    }
}
