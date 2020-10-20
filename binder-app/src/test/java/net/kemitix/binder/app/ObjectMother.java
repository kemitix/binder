package net.kemitix.binder.app;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.HtmlManuscript;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ObjectMother {

    private final BinderConfig binderConfig = mock(BinderConfig.class);

    private final YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader = new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
    private final Section section = mock(Section.class);

    public HtmlManuscript htmlManuscript() {
        return manuscriptLoader.htmlManuscript(mdManuscript(), markdownToHtml());
    }

    public MarkdownToHtml markdownToHtml() {
        MarkdownToHtmlProducer markdownToHtmlProducer = new MarkdownToHtmlProducer();
        VelocityProvider velocityProvider = new VelocityProvider();
        MdManuscript mdManuscript = mdManuscript();
        return markdownToHtmlProducer
                .markdownToHtml(
                        new TemplateEngine(
                                velocityProvider.velocityEngine(),
                                velocityProvider.context(mdManuscript)),
                        mdManuscript);
    }

    public MdManuscript mdManuscript() {
        return manuscriptLoader.mdManuscript(metadata());
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
