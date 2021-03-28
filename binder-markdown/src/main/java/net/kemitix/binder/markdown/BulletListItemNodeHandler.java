package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Context;

import java.util.stream.Stream;

public interface BulletListItemNodeHandler<T, R>
        extends NodeHandler<T, R> {

    default Class<? extends Node> getNodeClass() {
        return BulletListItem.class;
    }

    @Override
    default Stream<T> body(
            Node node,
            Stream<T> content,
            Context<R> context
    ) {
        BulletListItem item = (BulletListItem) node;
        return bulletListItemBody(
                item.getChildChars().unescape(),
                context
        );
    }

    Stream<T> bulletListItemBody(String text, Context<R> context);

}
