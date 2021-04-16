package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.HtmlInlineNodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Epub
@ApplicationScoped
public class HtmlInlineEpubNodeHandler
        implements HtmlInlineNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

}
