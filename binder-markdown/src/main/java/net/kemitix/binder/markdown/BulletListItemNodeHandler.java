package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;

import java.util.stream.Stream;

public interface BulletListItemNodeHandler<T>
        extends NodeHandler<T>{


    default Class<? extends Node> getNodeClass() {
        return Heading.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content) {
        BulletListItem item = (BulletListItem) node;
        return Stream.concat(
                bulletListItemBody(
                        item.getChildChars().unescape()
                ),
                content);
    }

    Stream<T> bulletListItemBody(String text);

}
