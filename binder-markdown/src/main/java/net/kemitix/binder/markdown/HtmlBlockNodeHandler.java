package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.HtmlBlock;
import com.vladsch.flexmark.util.ast.Node;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface HtmlBlockNodeHandler<T>
        extends NodeHandler<T> {

    @Override
    default Class<? extends Node> getNodeClass() {
        return HtmlBlock.class;
    }

}
