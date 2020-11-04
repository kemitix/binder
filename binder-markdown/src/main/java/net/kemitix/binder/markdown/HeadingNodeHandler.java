package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;

import java.util.Arrays;
import java.util.stream.Stream;

public interface HeadingNodeHandler
        extends NodeHandler {

    default Class<? extends Node> getNodeClass() {
        return Heading.class;
    }

    @Override
    default Object[] body(Node node, Object[] content) {
        Heading heading = (Heading) node;
        return Stream.concat(
                Stream.of(
                        headingBody(
                                heading.getLevel(),
                                heading.getText().unescape())),
                Arrays.stream(content)
        ).toArray();
    }

    Object[] headingBody(int level, String text);

}
