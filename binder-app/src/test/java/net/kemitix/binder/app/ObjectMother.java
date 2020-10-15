package net.kemitix.binder.app;

import net.kemitix.binder.app.docx.DefaultDocxTocItemRenderer;
import net.kemitix.binder.app.docx.DocxFactory;
import net.kemitix.binder.app.docx.DocxRenderer;
import net.kemitix.binder.app.docx.DocxSectionRenderer;
import net.kemitix.binder.app.docx.DocxTocItemRenderer;
import net.kemitix.binder.app.docx.HtmlDocxRenderer;
import net.kemitix.binder.app.docx.PlateDocxRenderer;
import net.kemitix.binder.app.docx.TocDocxRenderer;
import net.kemitix.binder.app.epub.DefaultEpubTocItemRenderer;
import net.kemitix.binder.app.epub.EpubFactory;
import net.kemitix.binder.app.epub.EpubRenderer;
import net.kemitix.binder.app.epub.EpubSectionRenderer;
import net.kemitix.binder.app.epub.EpubTocItemRenderer;
import net.kemitix.binder.app.epub.HtmlEpubRenderer;
import net.kemitix.binder.app.epub.StoryEpubTocItemRenderer;
import net.kemitix.binder.app.epub.TocEpubRenderer;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ObjectMother {

    private final BinderConfig binderConfig = mock(BinderConfig.class);
    private final ObjectFactory objectFactory = Context.getWmlObjectFactory();

    private final YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader = new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
    private final Section section = mock(Section.class);

    public DocxFactory docxFactory() {
        return new DocxFactory();
    }

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

    public EpubFactory epubFactory() {
        return new EpubFactory(binderConfig, htmlManuscript(),
                epubHtmlSectionRenderer());
    }

    private EpubSectionRenderer epubHtmlSectionRenderer() {
        List<EpubTocItemRenderer> tocItemRenderers = new ArrayList<>();
        tocItemRenderers.add(new DefaultEpubTocItemRenderer());
        tocItemRenderers.add(new StoryEpubTocItemRenderer());
        List<EpubRenderer> epubRenderers = new ArrayList<>();
        epubRenderers.add(new HtmlEpubRenderer());
        epubRenderers.add(new TocEpubRenderer(htmlManuscript(), new InstanceStream<>(tocItemRenderers)));
        return new EpubSectionRenderer(new InstanceStream<>(epubRenderers));
    }

    private DocxSectionRenderer docxHtmlSectionRenderer() {
        List<DocxTocItemRenderer> tocItemRenderers = new ArrayList<>();
        tocItemRenderers.add(new DefaultDocxTocItemRenderer(objectFactory));
        List<DocxRenderer> renderers = new ArrayList<>();
        renderers.add(new HtmlDocxRenderer());
        renderers.add(new PlateDocxRenderer());
        renderers.add(new TocDocxRenderer(htmlManuscript(), objectFactory(), new InstanceStream<>(tocItemRenderers)));
        return new DocxSectionRenderer(new InstanceStream<>(renderers));
    }

    private ObjectFactory objectFactory() {
        return objectFactory;
    }

}
