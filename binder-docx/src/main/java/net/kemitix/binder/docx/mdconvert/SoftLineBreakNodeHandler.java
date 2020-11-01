package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.util.ast.Node;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class SoftLineBreakNodeHandler
        implements NodeHandler {

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return SoftLineBreak.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(Arrays.asList(content));
        return objects.toArray();
    }
}
