package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.ast.Link;
import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.LinkNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class LinkEpubNodeHandler
        implements LinkNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler  {

    @Override
    public Stream<String> link(Link link, Context<EpubRenderHolder> context) {
        String text = link.getText().toString();
        String url = link.getUrl().toString();
        return Stream.of("""
                <a href="%s">%s</a>""".formatted(url, text));
    }

}
