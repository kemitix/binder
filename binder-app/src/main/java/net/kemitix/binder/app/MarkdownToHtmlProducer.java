package net.kemitix.binder.app;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MarkdownToHtmlProducer {

    @Produces
    MarkdownToHtml markdownToHtml(
            TemplateEngine templateEngine,
            Parser parser,
            HtmlRenderer renderer
    ) {
        return (section, mdManuscript) ->
        {
            String markdown = section.getMarkdown();
            String htmlBodyTemplate = renderer.render(parser.parse(markdown));
            return templateEngine.resolve(htmlBodyTemplate, section, mdManuscript);
        };
    }

}
