package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.FootnoteBlockNodeHandler;
import net.kemitix.binder.markdown.FootnoteBody;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteBlockEpubNodeHandler
        implements FootnoteBlockNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> footnoteBlockBody(FootnoteBody<String> footnoteBody) {
        return Stream.empty();
    }
}
