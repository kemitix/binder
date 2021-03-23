package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.EpubNodeHandler;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.footnote.FootnoteBodyNodeHandler;
import net.kemitix.binder.spi.Footnote;
import org.jsoup.Jsoup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteBodyEpubNodeHandler
        implements FootnoteBodyNodeHandler<String>, EpubNodeHandler {

    private final EpubFootnoteStore footnoteStore;

    @Inject
    public FootnoteBodyEpubNodeHandler(FootnoteStoreEpubProvider footnoteStoreEpubProvider) {
        this.footnoteStore = footnoteStoreEpubProvider.footnoteStore();
    }

    @Override
    public Stream<String> footnoteBody(
            Footnote.Ordinal ordinal,
            Stream<String> content,
            Context context
    ) {
        var body = content.collect(Collectors.joining())
                .replaceAll(" ~PARA~ ", "</p><p>");
        var element = Jsoup.parseBodyFragment(body).body();
        var placeholder = EpubFootnote.placeholder("");
        var html = EpubFootnote.content(element.html());
        var footnote =
                Footnote.create(ordinal, placeholder, Stream.of(html));
        footnoteStore.add(context.getName(), ordinal, footnote);
        return Stream.of(html.getValue());
    }
}
