package net.kemitix.binder.app;

import coza.opencollab.epub.creator.model.EpubBook;
import net.kemitix.binder.app.BinderConfig;
import net.kemitix.binder.app.HtmlManuscript;
import net.kemitix.binder.app.ManuscriptLoader;
import net.kemitix.binder.app.MarkdownToHtml;
import net.kemitix.binder.app.MdManuscript;
import net.kemitix.binder.app.Metadata;
import net.kemitix.binder.app.SectionLoader;
import net.kemitix.binder.app.YamlLoader;
import net.kemitix.binder.app.docx.DocxBook;
import net.kemitix.binder.app.docx.DocxContentFactory;
import net.kemitix.binder.app.docx.DocxFactory;
import net.kemitix.binder.app.docx.DocxWriter;
import net.kemitix.binder.app.epub.EpubContentFactory;
import net.kemitix.binder.app.epub.EpubFactory;
import net.kemitix.binder.app.epub.EpubWriter;
import org.apache.velocity.VelocityContext;
import org.docx4j.convert.in.xhtml.XHTMLImporter;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ObjectMother {

    private final BinderConfig binderConfig = mock(BinderConfig.class);
    private final XHTMLImporter xhtmlImporter = mock(XHTMLImporter.class);

    private final YamlLoader yamlLoader = new YamlLoader();
    private final SectionLoader sectionLoader = new SectionLoader(binderConfig, yamlLoader);
    private final ManuscriptLoader manuscriptLoader = new ManuscriptLoader(sectionLoader, yamlLoader);
    private final DocxContentFactory docxContentFactory = new DocxContentFactory(xhtmlImporter);
    private final Section section = mock(Section.class);
    private final EpubContentFactory epubContentFactory = new EpubContentFactory();

    public DocxFactory docxFactory() {
        return  new DocxFactory(binderConfig, htmlManuscript(), docxContentFactory);
    }

    public DocxContentFactory docxContentFactory() {
        return docxContentFactory;
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
        return new EpubFactory(binderConfig, htmlManuscript(), epubContentFactory);
    }
}
