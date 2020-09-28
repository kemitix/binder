package net.kemitix.binder.app;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MarkdownToHtmlProducer {

    @Produces
    MarkdownToHtml markdownToHtml(
            TemplateEngine templateEngine,
            MdManuscript mdManuscript
    ) {
        MutableDataSet dataSet = new MutableDataSet();
        Parser parser = Parser.builder(dataSet).build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return section ->
                renderTemplate(
                        renderer.render(parser.parse(section.getMarkdown())),
                        section,
                        mdManuscript,
                        templateEngine);
    }

    private String renderTemplate(
            String rawHtml,
            Section section,
            MdManuscript mdManuscript,
            TemplateEngine templateEngine) {
        return "<html><head><title>%s</title></head>\n<body>\n\n%s\n</body>\n</html>"
                .formatted(
                        section.getTitle(),
                        templateEngine.resolve(rawHtml, section, mdManuscript));
    }

}
