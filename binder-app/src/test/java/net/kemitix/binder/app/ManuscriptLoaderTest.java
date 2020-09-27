package net.kemitix.binder.app;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ManuscriptLoaderTest
        implements WithAssertions {

    AtomicReference<File> scanDirectory = new AtomicReference<>();

    BinderConfig binderConfig = new BinderConfig() {
        @Override
        public File getScanDirectory() {
            return scanDirectory.get();
        }
    };

    SectionLoader sectionLoader;

    private final YamlLoader yamlLoader = new YamlLoader();

    ManuscriptLoader manuscriptLoader;

    private File validDirectory = new File(getClass().getResource("valid").getPath());
    private File missingDirectory = new File("missing");
    private File invalidDirectory = new File(getClass().getResource("invalid").getPath());
    private TemplateEngine templateEngine = new TemplateEngine();

    @BeforeEach
    public void setUp() {
        sectionLoader = new SectionLoader(binderConfig, yamlLoader);
        manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader, templateEngine);
    }

    @Nested
    @DisplayName("ManuscriptMetadata")
    public class MetadataTests {

        @Test
        @DisplayName("Load file successfully")
        public void loadFileOkay() throws IOException {
            //given
            scanDirectory.set(validDirectory);
            Metadata expected = new Metadata();
            expected.setId("my-id");
            expected.setIssue(999);
            expected.setLanguage("en");
            expected.setDate("2020-08-28");
            expected.setTitle("my-title");
            expected.setSubtitle("my-subtitle");
            expected.setKdpSubtitle("my-kdp-subtitle");
            expected.setDescription("my-description");
            expected.setIsbn("my-isbn");
            expected.setEditor("my-editor");
            expected.setCover("cover.jpg");
            expected.setCoverArtist("my-cover-artist");
            expected.setContents(List.of(
                    "prelude-1", "prelude-2",
                    "content-1", "content-3", "content-2",
                    "coda-1", "coda-2"));
            //when
            Metadata metadata = manuscriptLoader.metadata(binderConfig);
            //then
            assertThat(metadata)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @Test
        @DisplayName("When Binder directory is missing")
        public void whenBinderDirectoryIsMissing() {
            //given
            scanDirectory.set(missingDirectory);
            //then
            assertThatExceptionOfType(MissingBinderDirectory.class)
                    .isThrownBy(() ->
                            manuscriptLoader.metadata(binderConfig));
        }

        @Test
        @DisplayName("When Metadata file is missing")
        public void whenMetadataIsMissing() {
            //given
            scanDirectory.set(invalidDirectory);
            //then
            assertThatExceptionOfType(ManuscriptFormatException.class)
                    .isThrownBy(() ->
                            manuscriptLoader.metadata(binderConfig))
                    .withCauseInstanceOf(FileNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Manuscript contents")
    public class ManuscriptContentsTests {

        private Metadata metadata;

        @BeforeEach
        void setUp() throws IOException {
            scanDirectory.set(validDirectory);
            metadata = manuscriptLoader.metadata(binderConfig);
        }

        @Test
        void loadAndParsePrelude1() {
            //when
            Manuscript manuscript = manuscriptLoader.manuscript(metadata);
            //then
            List<Section> prelude1s = manuscript.getContents()
                    .stream()
                    .filter(section -> "prelude-1".equals(section.getName()))
                    .collect(Collectors.toList());
            assertThat(prelude1s).hasSize(1);
            assertThat(prelude1s).satisfies(preludes -> {
                Section prelude = preludes.get(0);
                assertThat(prelude.getName()).isEqualTo("prelude-1");
                assertThat(prelude.getFilename()).isEqualTo(
                        validDirectory.toPath()
                                .resolve("prelude-1.md").toFile());
                assertThat(prelude.getTitle()).isEqualTo("test prelude 1 title");
                assertThat(prelude.getMarkdown())
                        .isEqualTo("# Document Title\n\ndocument body");
                assertThat(prelude.isToc()).isFalse();
            });
        }

        @Test
        void loadAndParsePrelude2() {
            //when
            Manuscript manuscript = manuscriptLoader.manuscript(metadata);
            //then
            List<Section> prelude1s = manuscript.getContents()
                    .stream()
                    .filter(section -> "prelude-2".equals(section.getName()))
                    .collect(Collectors.toList());
            assertThat(prelude1s).hasSize(1);
            assertThat(prelude1s).satisfies(preludes -> {
                Section prelude = preludes.get(0);
                assertThat(prelude.getName()).isEqualTo("prelude-2");
                assertThat(prelude.getFilename()).isEqualTo(
                        validDirectory.toPath()
                                .resolve("prelude-2.md").toFile());
                assertThat(prelude.getTitle()).isEqualTo("test prelude 2 title");
                assertThat(prelude.getMarkdown())
                        .isEqualTo("# Document Title 2\n\ndocument body");
                assertThat(prelude.isToc()).isTrue();
            });
        }
    }
}