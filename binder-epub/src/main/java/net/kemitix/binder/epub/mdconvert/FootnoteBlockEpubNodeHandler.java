package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import net.kemitix.binder.markdown.FootnoteBlockNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteBlockEpubNodeHandler
        implements FootnoteBlockNodeHandler<String> {
    @Override
    public Stream<String> footnoteBlockBody(
            FootnoteBlock footnoteBlock,
            Stream<String> content
    ) {
        return Stream.concat(
                Stream.of(footnoteBlock.getText().unescape()),
                content
        );
    }
}
