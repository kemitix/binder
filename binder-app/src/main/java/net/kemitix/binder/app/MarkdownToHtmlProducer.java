package net.kemitix.binder.app;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.parser.Parser;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class MarkdownToHtmlProducer {

    @Produces
    MarkdownToHtml markdownToHtml() {
        MutableDataSet dataSet = new MutableDataSet();
        Parser parser = Parser.builder(dataSet).build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return markdown -> renderer.render(parser.parse(markdown));
    }

}
