package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.FootnoteAnchor;
import net.kemitix.binder.markdown.FootnoteNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteEpubNodeHandler
        implements FootnoteNodeHandler<String>, EpubNodeHandler  {

    @Override
    public Stream<String> footnoteBody(FootnoteAnchor footnoteAnchor) {
        String storeName = footnoteAnchor.getStoreName();
        String oridinal = footnoteAnchor.getOridinal();
        return Stream.of("""
                        <sup class="footnoteAnchor-anchor"><a
                            href="../%2$s#note-%1$s"
                            title="%1$s" 
                            id="back-link=%1$s">%1$s</a></sup> """
                .formatted(oridinal, storeName)
        );
    }
}
