package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxRenderHolder;
import net.kemitix.binder.markdown.HtmlEntityNodeHandler;
import net.kemitix.binder.markdown.UnsupportedHtmlEntityException;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Docx
@ApplicationScoped
public class HtmlEntityDocxNodeHandler
        implements HtmlEntityNodeHandler<Object, DocxRenderHolder> {

    private final Map<String, String> allowedEntities = Map.of(
            "&amp;", "&"
    );

    @Override
    public Stream<Object> body(
            Node node,
            Stream<Object> content,
            Context<DocxRenderHolder> context
    ) {
        String entity = node.getChars().toString();
        if (allowedEntities.containsKey(entity)) {
            DocxFacade docx = context.getRendererHolder().getRenderer();
            return Stream.of(docx.r(docx.t(allowedEntities.get(entity))));
        }
        throw new UnsupportedHtmlEntityException(entity, node,
                content.collect(Collectors.toList()), context);
    }


}
