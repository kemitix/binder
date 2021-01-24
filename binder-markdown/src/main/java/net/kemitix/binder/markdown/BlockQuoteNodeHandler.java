package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BlockQuote;
import com.vladsch.flexmark.util.ast.Node;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BlockQuoteNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return BlockQuote.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context context) {
        List<T> collect = content.collect(Collectors.toList());
        if (collect.size() != 1) {
            //noinspection unchecked
            throw new MarkdownConversionException(
                    "Not passed a single content item: %d sent".formatted(collect.size()),
                    node, (List<Object>) collect, context);
        }
        return blockQuoteBody(collect.get(0));
    }

    Stream<T> blockQuoteBody(T content);

}
