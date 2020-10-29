package net.kemitix.binder.app;

import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;
import net.kemitix.binder.spi.MdManuscript;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Arrays;

@ApplicationScoped
public class MarkdownToHtmlProducer {

    @Produces
    MarkdownToHtml markdownToHtml(
            TemplateEngine templateEngine,
            MdManuscript mdManuscript
    ) {
        MutableDataSet dataSet = new MutableDataSet();
        dataSet.set(Parser.EXTENSIONS, Arrays.asList(FootnoteExtension.create()));
        Parser parser = Parser.builder(dataSet).build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return section ->
        {
            String markdown = section.getMarkdown();
            String htmlBodyTemplate = renderer.render(parser.parse(markdown));
            return templateEngine.resolve(htmlBodyTemplate, section);
        };
    }

}
