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
                heading.getText().unescape());
    }

    Stream<T> headingBody(int level, String text);

}
