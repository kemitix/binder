package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.FootnoteNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteEpubNodeHandler
        implements FootnoteNodeHandler<String>  {
    @Override
    public Stream<String> footnoteBody(String oridinal, String text) {
        return Stream.of(
                "(Footnote #%s)"
                        .formatted(oridinal)
        );
    }
}
