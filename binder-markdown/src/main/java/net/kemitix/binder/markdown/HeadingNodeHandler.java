package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface HeadingNodeHandler<T, R extends RenderHolder<?>>
        extends NodeHandler<T, R> {

    default Class<? extends Node> getNodeClass() {
        return Heading.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context<R> context) {
        Heading heading = (Heading) node;
        return headingBody(
                heading.getLevel(),
                heading.getText().unescape(),
                context);
    }

    default Stream<T> headingBody(
            int level,
            String text,
            Context<R> context
    ) {
        if (context.isType(Section.Type.story)) {
            return breakHeader(text, context);
        }
        return hierarchicalHeader(level, text, context);
    }

    Stream<T> hierarchicalHeader(int level, String text, Context<R> context);

    default Stream<T> breakHeader(String text, Context<R> context) {
        if ("_BINDER_CUT_ START".equals(text)) return Stream.empty();
        if ("_BINDER_CUT_ END".equals(text)) return Stream.empty();
        if (text.isBlank()) return blankBreak(context);
        return namedBreak(text, context);
    }

    Stream<T> blankBreak(Context<R> context);

    Stream<T> namedBreak(String text, Context<R> context);

}
