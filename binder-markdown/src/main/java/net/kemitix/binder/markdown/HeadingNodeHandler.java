package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface HeadingNodeHandler<T>
        extends NodeHandler<T> {

    default Class<? extends Node> getNodeClass() {
        return Heading.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context context) {
        Heading heading = (Heading) node;
        return headingBody(
                heading.getLevel(),
                heading.getText().unescape(),
                context);
    }

    default Stream<T> headingBody(
            int level,
            String text,
            Context context
    ) {
        if (context.isType(Section.Type.story)) {
            return breakHeader(text);
        }
        return hierarchicalHeader(level, text, context);
    }

    Stream<T> hierarchicalHeader(int level, String text, Context context);

    default Stream<T> breakHeader(String text) {
        if (text.isBlank()) return blankBreak();
        return namedBreak(text);
    }

    Stream<T> blankBreak();

    Stream<T> namedBreak(String text);

}
