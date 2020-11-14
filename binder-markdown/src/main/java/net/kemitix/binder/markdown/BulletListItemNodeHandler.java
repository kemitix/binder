package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.BulletListItem;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.spi.Section;

import java.util.stream.Stream;

public interface BulletListItemNodeHandler<T>
        extends NodeHandler<T>{


    default Class<? extends Node> getNodeClass() {
        return BulletListItem.class;
    }

    @Override
    default Stream<T> body(Node node, Stream<T> content, Context context) {
        BulletListItem item = (BulletListItem) node;
        return bulletListItemBody(
                item.getChildChars().unescape()
        );
    }

    Stream<T> bulletListItemBody(String text);

}
