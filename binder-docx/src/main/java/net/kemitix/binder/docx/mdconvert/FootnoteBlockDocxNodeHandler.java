package net.kemitix.binder.docx.mdconvert;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Node;
import net.kemitix.binder.markdown.FootnoteBlockNodeHandler;
import net.kemitix.binder.markdown.NodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Docx
@ApplicationScoped
public class FootnoteBlockDocxNodeHandler
        implements FootnoteBlockNodeHandler<Object> {

}
