package net.kemitix.binder.app;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import net.kemitix.binder.spi.MdManuscript;

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
        {
            String markdown = section.getMarkdown();
            String htmlBodyTemplate = renderer.render(parser.parse(markdown));
            String htmlBody =
                    templateEngine.resolve(htmlBodyTemplate, section);
            return ("""
                    <html><head><title>%s</title></head>
                    <body>

                    %s
                    </body>
                    </html>""")
                    .formatted(section.getTitle(), htmlBody);
        };
    }

}
