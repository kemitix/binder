package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.EpubNodeHandler;
import net.kemitix.binder.markdown.footnote.FootnoteAnchor;
import net.kemitix.binder.markdown.footnote.FootnoteAnchorNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteAnchorEpubNodeHandler
        implements FootnoteAnchorNodeHandler<String>, EpubNodeHandler {

    @Override
    public Stream<String> footnoteAnchor(FootnoteAnchor footnoteAnchor) {
        String htmlFile = footnoteAnchor.getHtmlFile();
        String ordinal = footnoteAnchor.getOrdinal();
        return Stream.of("""
                        <sup class="footnoteAnchor-anchor"><a
                            href="%2$s"
                            title="%1$s" 
                            id="back-link-%1$s">%1$s</a></sup> """
                .formatted(ordinal, htmlFile)
        );
    }
}
