package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Arrays;

@ApplicationScoped
public class FlexmarkProducers {

    @Produces
    @ApplicationScoped
    public HtmlRenderer htmlRenderer() {
        return HtmlRenderer.builder().build();
    }

    @Produces
    @ApplicationScoped
    public Parser parser() {
        MutableDataSet dataSet = new MutableDataSet();
        dataSet.set(Parser.EXTENSIONS, Arrays.asList(
                StrikethroughExtension.create(),
                FootnoteExtension.create()));
        return Parser.builder(dataSet).build();
    }

}
