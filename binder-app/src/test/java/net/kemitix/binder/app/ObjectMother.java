package net.kemitix.binder.app;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import net.kemitix.binder.markdown.FlexmarkProducers;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ObjectMother {

    private final BinderConfig binderConfig = mock(BinderConfig.class);

    private final YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader = new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
    private final Section section = mock(Section.class);
    private final TemplateEngine templateEngine = mock(TemplateEngine.class);

    public ObjectMother() {
        given(templateEngine.resolve(anyString(), any(Section.class), any(MdManuscript.class)))
                .willAnswer((Answer<String>) invocation ->
                        invocation.getArgument(0));
    }

    public HtmlManuscript htmlManuscript() {
        return manuscriptLoader.htmlManuscript(mdManuscript(), markdownToHtml());
    }

    public MarkdownToHtml markdownToHtml() {
        MarkdownToHtmlProducer markdownToHtmlProducer = new MarkdownToHtmlProducer();
        VelocityProvider velocityProvider = new VelocityProvider();
        return markdownToHtmlProducer
                .markdownToHtml(
                        new TemplateEngine(
                                velocityProvider.velocityEngine(),
                                velocityProvider.velocityContext()),
                        markdownParser(),
                        htmlRenderer());
    }

    private HtmlRenderer htmlRenderer() {
        return new FlexmarkProducers().htmlRenderer();
    }

    private Parser markdownParser() {
        return new FlexmarkProducers().parser();
    }

    public MdManuscript mdManuscript() {
        return manuscriptLoader.mdManuscript(metadata(), templateEngine);
    }

    public Metadata metadata() {
        return manuscriptLoader.metadata(binderConfig);
    }

    public ManuscriptLoader manuscriptLoader() {
        return manuscriptLoader;
    }

    public YamlLoader yamlLoader() {
        return yamlLoader;
    }

    public SectionLoader sectionLoader() {
        return sectionLoader;
    }

    public BinderConfig binderConfig() {
        given(binderConfig.getFile(anyString(), anyString()))
                .willCallRealMethod();
        return binderConfig;
    }

    public Section section() {
        return section;
    }

}
