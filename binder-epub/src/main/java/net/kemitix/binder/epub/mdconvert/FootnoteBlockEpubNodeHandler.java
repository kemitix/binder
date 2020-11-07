package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import net.kemitix.binder.markdown.FootnoteBlockNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class FootnoteBlockEpubNodeHandler
        implements FootnoteBlockNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> footnoteBlockBody(
            FootnoteBlock footnoteBlock,
            Stream<String> content
    ) {
        // TODO - store in a separate per-story file just for footnotes
        // - only create the file if needed
        // - little/no change to interfaces in markdown package
        return Stream.of("""
                                <aside id="n%s" epub:type="footnote">
                                %s
                                </aside>
                                """
                .formatted(
                        footnoteBlock.getText().unescape(),
                        collect(content))
        );
    }
}
