package net.kemitix.binder.app;

import net.kemitix.binder.spi.ManuscriptFormatException;
import net.kemitix.binder.spi.Section;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class YamlLoader {

    public  <T> T loadFile(
            File file,
            Class<T> theRoot
    ) {
        try {
            String header = Files.readString(requiredFile(file).toPath());
            return parseYamlFromFile(file, theRoot, header);
        } catch (IOException e) {
            throw new ManuscriptFormatException("Error loading", theRoot, file, e);
        }
    }

    public Section loadSectionFile(
            File file
    ) {
        List<String> lines = loadRequiredFile(file);
        List<String> header = lines
                .stream()
                .dropWhile("---"::equals)
                .takeWhile(line -> !line.equals("---"))
                .collect(Collectors.toList());
        int headerLines = header.size() + 2;
        String body = lines.stream().skip(headerLines)
                .collect(Collectors.joining(System.lineSeparator()));
        Section section = parseYamlFromFile(file, Section.class,
                String.join(System.lineSeparator(), header));
        section.setMarkdown(body);
        return section;
    }

    private List<String> loadRequiredFile(File file) {
        try {
            return Files.readAllLines(requiredFile(file).toPath());
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + file);
        }
    }

    private File requiredFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getCanonicalPath());
        }
        return file;
    }

    private <T> T parseYamlFromFile(
            File file,
            Class<T> theRoot,
            String header
    ) {
        try {
            Yaml yaml = new Yaml(new Constructor(theRoot));
            T loaded = yaml.load(header);
            if (loaded == null) {
                throw new ManuscriptFormatException("File not compatible", theRoot, file);
            }
            return loaded;
        } catch (YAMLException e) {
            throw new ManuscriptFormatException("Error parsing", theRoot, file, e);
        }

    }

}
