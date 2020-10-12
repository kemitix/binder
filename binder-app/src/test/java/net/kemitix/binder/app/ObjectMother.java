package net.kemitix.binder.app;

import coza.opencollab.epub.creator.model.Content;
import net.kemitix.binder.app.docx.DocxFactory;
import net.kemitix.binder.app.docx.DocxHtmlSectionRenderer;
import net.kemitix.binder.app.docx.DocxRenderer;
import net.kemitix.binder.app.docx.HtmlDocxContentSectionRenderer;
import net.kemitix.binder.app.docx.PlateDocxContentSectionRenderer;
import net.kemitix.binder.app.docx.TocDocxContentSectionRenderer;
import net.kemitix.binder.app.epub.EpubFactory;
import net.kemitix.binder.app.epub.EpubHtmlSectionRenderer;
import net.kemitix.binder.app.epub.HtmlEpubRenderer;
import net.kemitix.binder.app.epub.TocEpubRenderer;
import org.docx4j.convert.in.xhtml.XHTMLImporter;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ObjectMother {

    private final BinderConfig binderConfig = mock(BinderConfig.class);
    private final XHTMLImporter xhtmlImporter = mock(XHTMLImporter.class);

    private final YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader = new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
    private final Section section = mock(Section.class);

    public DocxFactory docxFactory() {
        return new DocxFactory();
    }

    public XHTMLImporter xhtmlImporter() {
        return xhtmlImporter;
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

    private EpubHtmlSectionRenderer epubHtmlSectionRenderer() {
        List<Renderer<HtmlSection, Content>> renderers = new ArrayList<>();
        renderers.add(new HtmlEpubRenderer());
        renderers.add(new TocEpubRenderer(htmlManuscript()));
        return new EpubHtmlSectionRenderer(new InstanceStream<>(renderers));
    }

    private DocxHtmlSectionRenderer docxHtmlSectionRenderer() {
        List<DocxRenderer> renderers = new ArrayList<>();
        renderers.add(new HtmlDocxContentSectionRenderer(xhtmlImporter));
        renderers.add(new PlateDocxContentSectionRenderer());
        renderers.add(new TocDocxContentSectionRenderer(htmlManuscript()));
        return new DocxHtmlSectionRenderer(new InstanceStream<>(renderers));
    }

}
