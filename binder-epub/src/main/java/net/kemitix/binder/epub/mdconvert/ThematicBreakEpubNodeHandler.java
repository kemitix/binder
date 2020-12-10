package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.ThematicBreakNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class ThematicBreakEpubNodeHandler
        implements ThematicBreakNodeHandler<String> {

    @Override
    public Stream<String> body(Node node, Stream<String> content, Context context) {
        return Stream.of(
                "<hr/>"
        );
    }

}
