package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.superscript.Superscript;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SuperscriptNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return Superscript.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        List<T> collect = content.collect(Collectors.toList());
        if (collect.size() != 1) {
            //noinspection unchecked
            throw new MarkdownConversionException(
                    "Not passed a single content item: %d sent".formatted(collect.size()),
                    node, (List<Object>) collect, context);
        }
        return superscriptBody(collect.get(0), context);
    }

    Stream<T> superscriptBody(T content, Context<R> context);

}
