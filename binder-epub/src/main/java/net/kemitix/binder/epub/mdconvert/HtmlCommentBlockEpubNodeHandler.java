package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.HtmlCommentBlockNodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Epub
@ApplicationScoped
public class HtmlCommentBlockEpubNodeHandler
        implements HtmlCommentBlockNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {
}
