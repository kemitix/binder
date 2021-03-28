package net.kemitix.binder.docx;

import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class StoryDocxTocItemRenderer
        implements DocxTocItemRenderer {

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<Object> render(Section source, Context<DocxRenderHolder> context) {
        var docx = context.getRendererHolder().getRenderer();
        return Stream.of(
                docx.tocItem(
                        Integer.toString(source.getPage()),
                        source.getTitle()));
    }
}
