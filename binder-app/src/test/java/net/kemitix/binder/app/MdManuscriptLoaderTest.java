package net.kemitix.binder.app;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptFormatException;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MdManuscriptLoaderTest
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
    private TemplateEngine templateEngine = mock(TemplateEngine.class);

    @BeforeEach
    public void setUp() {
        sectionLoader = new SectionLoader(binderConfig, yamlLoader);
        manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
        given(templateEngine.resolve(anyString(), any(Section.class), any(MdManuscript.class)))
                .willAnswer((Answer<String>) invocation ->
                        invocation.getArgument(0));
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
                    "prelude-1", "toc",
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
    public class MdManuscriptContentsTests {

        private Metadata metadata;

        @BeforeEach
        void setUp() throws IOException {
            scanDirectory.set(validDirectory);
            metadata = manuscriptLoader.metadata(binderConfig);
        }

        @Test
        void loadAndParsePrelude1() {
            //when
            MdManuscript mdManuscript =
                    manuscriptLoader.mdManuscript(metadata, templateEngine);
            //then
            List<Section> prelude1s = mdManuscript.getContents()
                    .stream()
                    .filter(section -> Section.name("prelude-1").equals(section.getName()))
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
            MdManuscript mdManuscript =
                    manuscriptLoader.mdManuscript(metadata, templateEngine);
            //then
            List<Section> prelude1s = mdManuscript.getContents()
                    .stream()
                    .filter(section -> Section.name("toc").equals(section.getName()))
                    .collect(Collectors.toList());
            assertThat(prelude1s).hasSize(1);
            assertThat(prelude1s).satisfies(preludes -> {
                Section prelude = preludes.get(0);
                assertThat(prelude.getName()).isEqualTo("toc");
                assertThat(prelude.getFilename()).isEqualTo(
                        validDirectory.toPath()
                                .resolve("toc.md").toFile());
                assertThat(prelude.getTitle()).isNullOrEmpty();
                assertThat(prelude.getMarkdown())
                        .isEqualTo("");
                assertThat(prelude.isToc()).isTrue();
            });
        }
    }
}