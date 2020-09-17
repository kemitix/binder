package net.kemitix.binder.app;

import net.kemitix.binder.app.epub.EpubContentFactory;
import net.kemitix.binder.app.epub.EpubFactory;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BinderTest
        implements WithAssertions {

    BinderConfig binderConfig = new BinderConfig() {
        @Override
        public File getScanDirectory() {
            return new File(getClass().getResource("valid").getPath());
        }
    };
    YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader =
            new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader =
            new ManuscriptLoader(sectionLoader, yamlLoader);
    ManuscriptMetadata metadata = manuscriptLoader.manuscriptMetadata(binderConfig);
    Manuscript manuscript = manuscriptLoader.manuscript(metadata);
    MarkdownToHtml markdownToHtml = new MarkdownToHtmlProducer()
            .markdownToHtml();
    HtmlFactory htmlFactory = new HtmlFactory(
            binderConfig,
            manuscript,
            markdownToHtml
    );
    EpubContentFactory epubContentFactory = new EpubContentFactory();
    EpubFactory epubFactory = new EpubFactory(binderConfig, manuscript, epubContentFactory);
    @Mock
    EpubWriter epubWriter;
    BinderApp app;

    @BeforeEach
    void setUp() {
        app = new BinderApp(
                htmlFactory,
                epubFactory,
                epubWriter
        );
    }

    @Test
    void createEpubBook() {
        //when
        app.run(new String[] {});
        //then
        verify(epubWriter).write(any());
    }

}
