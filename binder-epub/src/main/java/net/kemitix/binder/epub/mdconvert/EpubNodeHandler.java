package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.NodeHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface EpubNodeHandler
        extends NodeHandler<String, EpubRenderHolder>  {

    default String collect(Stream<String> content) {
        return content.collect(Collectors.joining());
    }

}
