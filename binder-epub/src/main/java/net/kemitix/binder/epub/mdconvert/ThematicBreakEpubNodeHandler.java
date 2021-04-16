package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.markdown.ThematicBreakNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class ThematicBreakEpubNodeHandler
        implements ThematicBreakNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    @Override
    public Stream<String> body(
            Node node,
            Stream<String> content,
            Context<EpubRenderHolder> context
    ) {
        return Stream.of(
                "<hr/>"
        );
    }

}
