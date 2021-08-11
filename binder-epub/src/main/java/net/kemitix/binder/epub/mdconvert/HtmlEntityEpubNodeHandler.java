package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.HtmlEntityNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HtmlEntityEpubNodeHandler
        implements HtmlEntityNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    private final List<String> allowedEntities = List.of(
            "&amp;"
    );

    @Override
    public Stream<String> body(
            Node node,
            Stream<String> content,
            Context<EpubRenderHolder> context
    ) {
        String entity = node.getChars().toString();
        if (allowedEntities.contains(entity)) {
            return Stream.of(entity);
        }
        throw new UnsupportedHtmlEntityException(entity, node,
                content.collect(Collectors.toList()), context);
    }

}
