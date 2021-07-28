package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Node;

import java.util.ArrayList;
import java.util.List;

public class Nodes {

    final List<Node> nodes;

    private Nodes(Node node) {
        nodes = new ArrayList<>();
        nodes.add(node);
        Node next = node.getNext();
        while (next != null) {
            nodes.add(next);
            next = next.getNext();
        }
    }

    public static Nodes of(Node node) {
        return new Nodes(node);
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }
}
