package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.HtmlEntityNodeHandler;

import javax.enterprise.context.ApplicationScoped;

@Epub
@ApplicationScoped
public class HtmlEntityEpubNodeHandler
        implements HtmlEntityNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

}
