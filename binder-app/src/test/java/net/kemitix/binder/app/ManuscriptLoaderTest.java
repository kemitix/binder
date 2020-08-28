package net.kemitix.binder.app;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ManuscriptLoaderTest
        implements WithAssertions {

    @Mock
    BinderConfig binderConfig;

    @Mock
    SectionLoader sectionLoader;

    ManuscriptLoader manuscriptLoader;

    private File validDirectory = new File(getClass().getResource("valid").getPath());
    private File missingDirectory = new File("missing");
    private File invalidDirectory = new File(getClass().getResource("invalid").getPath());

    @BeforeEach
    public void setUp() {
        manuscriptLoader = new ManuscriptLoader(sectionLoader);
        System.out.println("validDirectory = " + validDirectory);
        System.out.println("invalidDirectory = " + invalidDirectory);
    }

    @Nested
    @DisplayName("ManuscriptMetadata")
    public class ManuscriptMetadataTests {

        @Test
        @DisplayName("Load file successfully")
        public void loadFileOkay() throws IOException {
            //given
            given(binderConfig.getScanDirectory())
                    .willReturn(validDirectory);
            ManuscriptMetadata expected = new ManuscriptMetadata();
            expected.setId("my-id");
            expected.setIssue(999);
            expected.setDate("2020-08-28");
            expected.setTitle("my-title");
            expected.setSubtitle("my-subtitle");
            expected.setKdpSubtitle("my-kdp-subtitle");
            expected.setDescription("my-description");
            expected.setIsbn("my-isbn");
            expected.setEditor("my-editor");
            expected.setCover("my-cover");
            expected.setCoverArtist("my-cover-artist");
            expected.setPreludes(List.of("prelude-1", "prelude-2"));
            expected.setContents(List.of("content-1", "content-3", "content-2"));
            expected.setCodas(List.of("coda-1", "coda-2"));
            //when
            ManuscriptMetadata metadata = manuscriptLoader.manuscriptMetadata(binderConfig);
            //then
            assertThat(metadata)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
        }

        @Test
        @DisplayName("When Binder directory is missing")
        public void whenBinderDirectoryIsMissing() {
            //given
            given(binderConfig.getScanDirectory())
                    .willReturn(missingDirectory);
            //then
            assertThatExceptionOfType(MissingBinderDirectory.class)
                    .isThrownBy(() ->
                            manuscriptLoader.manuscriptMetadata(binderConfig));
        }

        @Test
        @DisplayName("When Metadata file is missing")
        public void whenMetadataIsMissing() {
            //given
            given(binderConfig.getScanDirectory())
                    .willReturn(invalidDirectory);
            //then
            assertThatExceptionOfType(MissingBinderYamlFile.class)
                    .isThrownBy(() ->
                            manuscriptLoader.manuscriptMetadata(binderConfig));
        }
    }

}