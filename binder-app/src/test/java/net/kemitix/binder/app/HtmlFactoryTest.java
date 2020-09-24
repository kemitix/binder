package net.kemitix.binder.app;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class HtmlFactoryTest
        implements WithAssertions {

    File validDirectory = new File(getClass().getResource("valid").getPath());
    AtomicReference<File> scanDirectory = new AtomicReference<>();
    BinderConfig binderConfig = () -> scanDirectory.get();
    YamlLoader yamlLoader = new YamlLoader();
    SectionLoader sectionLoader = new SectionLoader(binderConfig, yamlLoader);
    ManuscriptLoader manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
    Metadata metadata;
    Manuscript manuscript;
    MarkdownToHtml markdownToHtml = new MarkdownToHtmlProducer().markdownToHtml();

    HtmlFactory htmlFactory;

    @BeforeEach
    void setUp() {
        scanDirectory.set(validDirectory);
        metadata = manuscriptLoader.metadata(binderConfig);
        manuscript = manuscriptLoader.manuscript(metadata);
        htmlFactory = new HtmlFactory(binderConfig, manuscript, markdownToHtml);
    }

    @Test
    @DisplayName("Convert to HTML files")
    void convertToHtmlFiles() {
        //when
        htmlFactory.createAll();
        //then
        assertThat(expectedHtmlFiles()).allMatch(File::exists);
    }

    private Stream<File> expectedHtmlFiles() {
        Path outputDir = validDirectory.toPath().resolve("binder");
        return Arrays.stream(validDirectory.listFiles())
                .map(File::getName)
                .filter(n -> n.endsWith(".md"))
                .map(n -> n.replace(".md", ".html"))
                .map(outputDir::resolve)
                .map(Path::toFile);
    }
}