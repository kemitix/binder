package net.kemitix.binder.app;

import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Collections;

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
        dataSet.set(Parser.EXTENSIONS, Collections.singletonList(
                FootnoteExtension.create()));
        return Parser.builder(dataSet).build();
    }

}
