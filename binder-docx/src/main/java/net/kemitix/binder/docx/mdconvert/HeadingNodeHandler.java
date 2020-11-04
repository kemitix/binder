package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Docx
@ApplicationScoped
public class HeadingNodeHandler
        implements NodeHandler {

    private final DocxFacade docx;

    @Inject
    public HeadingNodeHandler(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Class<? extends Node> aClass) {
        return Heading.class.equals(aClass);
    }

    @Override
    public Object[] body(Node node, Object[] content) {
        Heading heading = (Heading) node;
        int level = heading.getLevel();
        String text = heading.getText().unescape();
        List<Object> objects = new ArrayList<>();
        objects.add(docx.heading(level, text));
        objects.addAll(Arrays.asList(content));
        return objects.toArray();
    }
}
