package net.kemitix.binder.app;

import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.model.EpubBook;
import net.kemitix.binder.app.docx.DocxContentFactory;
import net.kemitix.binder.app.docx.DocxFactory;
import net.kemitix.binder.app.docx.DocxWriter;
import net.kemitix.binder.app.epub.EpubContentFactory;
import net.kemitix.binder.app.epub.EpubFactory;
import net.kemitix.binder.app.epub.EpubWriter;
import org.apache.velocity.app.VelocityEngine;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.enterprise.inject.Instance;
import java.io.File;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
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
    VelocityEngine velocityEngine = new VelocityProvider().velocityEngine();
    TemplateEngine templateEngine = new TemplateEngine(velocityEngine);
    YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader =
            new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader =
            new ManuscriptLoader(sectionLoader, yamlLoader, templateEngine);
    Metadata metadata = manuscriptLoader.metadata(binderConfig);
    MdManuscript mdManuscript = manuscriptLoader.mdManuscript(metadata);
    MarkdownToHtml markdownToHtml = new MarkdownToHtmlProducer()
            .markdownToHtml(templateEngine, mdManuscript);
    HtmlFactory htmlFactory = new HtmlFactory(
            binderConfig,
            mdManuscript,
            markdownToHtml
    );
    EpubContentFactory epubContentFactory = new EpubContentFactory();
    EpubFactory epubFactory;

    DocxContentFactory docxContextFactory = new DocxContentFactory();
    DocxFactory docxFactory;

    @Mock EpubWriter epubWriter;
    @Mock DocxWriter docxWriter;

    BinderApp app;

    EpubBook epubBook;
    @Mock
    private Instance<ManuscriptWriter> writers;


    @BeforeEach
    void setUp() {
        HtmlManuscript htmlManuscript = manuscriptLoader.htmlManuscript(mdManuscript, markdownToHtml);
        epubFactory = new EpubFactory(binderConfig, htmlManuscript, epubContentFactory);
        docxFactory = new DocxFactory(binderConfig, htmlManuscript, docxContextFactory);
        given(writers.stream()).willReturn(Stream.of(epubWriter, docxWriter));
        app = new BinderApp(
                writers,
                htmlFactory,
                epubFactory,
                epubWriter,
                docxWriter,
                docxFactory);
        //when
        app.run(new String[] {});
        epubBook = epubFactory.create();
        //then
        verify(epubWriter).write();
    }


    @Captor
    ArgumentCaptor<EpubBook> writerCaptor;

    @Test
    void metadata() {
        assertThat(epubBook.getId()).isEqualTo("my-id");
        assertThat(epubBook.getLanguage()).isEqualTo("en");
        assertThat(epubBook.getTitle()).isEqualTo("my-title");
        assertThat(epubBook.getAuthor()).isEqualTo("my-editor");
    }

    @Test
    void coverImage() {
        Content cover = epubBook.getContents().get(0);
        assertThat(cover.getMediaType()).isEqualTo("image/jpeg");
        assertThat(cover.getHref()).isEqualTo("cover.jpg");
        assertThat(cover.getProperties()).isEqualTo("cover-image");
        assertThat(cover.getContent()).asHexString().startsWith("FFD8FFE000");
        assertThat(cover.getContent()).asHexString().endsWith("3F101FFFD9");
        assertThat(cover.getContent()).hasSize(542);
        assertThat(cover.isLinear()).isTrue();
        assertThat(cover.isSpine()).isFalse();
        assertThat(cover.isToc()).isFalse();
    }

    @Test
    void coverPage() {
        Content content = epubBook.getContents().get(1);
        assertThat(content.getMediaType()).isEqualTo("application/xhtml+xml");
        assertThat(content.getHref()).isEqualTo("cover.html");
        assertThat(content.getProperties()).isNull();
        assertThat(content.getContent()).asString()
                .contains("<title>Cover</title>")
                .contains("<body><img src=\"cover.jpg\" style=\"height:100%\"/></body>");
        assertThat(content.isLinear()).isTrue();
        assertThat(content.isSpine()).isTrue();
        assertThat(content.isToc()).isFalse();
    }

    @Test
    void titlePage() {
        Content content = epubBook.getContents().get(2);
        assertThat(content.getMediaType()).isEqualTo("application/xhtml+xml");
        assertThat(content.getHref()).isEqualTo("content/prelude-1.html");
        assertThat(content.getProperties()).isNull();
        assertThat(content.getContent()).asString()
                .startsWith("<html><head><title>test prelude 1 title</title></head>\n<body>")
                .contains("<h1>Document Title</h1>")
                .contains("<p>document body</p>")
                .endsWith("\n</body>\n</html>");
        assertThat(content.isLinear()).isTrue();
        assertThat(content.isSpine()).isTrue();
        assertThat(content.isToc()).isFalse();
    }

}
