package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.epub.mdconvert.Epub;
import net.kemitix.binder.epub.mdconvert.EpubNodeHandler;
import net.kemitix.binder.markdown.footnote.FootnoteAnchor;
import net.kemitix.binder.markdown.footnote.FootnoteAnchorNodeHandler;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.Footnote;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteAnchorEpubNodeHandler
        implements FootnoteAnchorNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    @Override
    public Stream<String> footnoteAnchor(
            FootnoteAnchor footnoteAnchor,
            Context<EpubRenderHolder> context
    ) {
        var htmlFile = footnoteAnchor.getHtmlFile();
        var ordinal = footnoteAnchor.getOrdinal();
        return Stream.of("""
                        <sup class="footnote-anchor"><a
                            id="back_note_%1$s"
                            href="%2$s#note_%1$s"
                            title="%1$s"
                            class="noteref">%1$s</a></sup> """
                .formatted(ordinal, htmlFile)
        );
    }
}
