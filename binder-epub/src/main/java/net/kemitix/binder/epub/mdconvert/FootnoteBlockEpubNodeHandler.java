package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.FootnoteBlockNodeHandler;
import net.kemitix.binder.markdown.FootnoteBody;
import net.kemitix.binder.markdown.MarkdownOutputException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteBlockEpubNodeHandler
        implements FootnoteBlockNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> footnoteBlockBody(FootnoteBody<String> footnoteBody) {
        var ordinal = footnoteBody.getOridinal();
        var body = footnoteBody.getContent().collect(Collectors.joining())
                .replaceAll(" ~PARA~ ", "</p><p>");
        var element = Jsoup.parseBodyFragment(body).body();
        var children = element.children();
        if (children.first().is("p")) {
            var sup = new Element("sup");;
            children.first().insertChildren(0, sup.text(ordinal));
            return Stream.of(element.html());
        } else {
            throw new MarkdownOutputException("Generated footnote body should start with a paragraph", body);
        }
    }
}
