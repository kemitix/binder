package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ast.BulletList;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.BulletListNodeHandler;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class BulletListDocxNodeHandler
        implements BulletListNodeHandler<Object> {

}
