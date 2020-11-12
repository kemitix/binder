package net.kemitix.binder.docx;

import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class StoryDocxTocItemRenderer
        implements DocxTocItemRenderer {

    private final DocxFacade docx;

    @Inject
    public StoryDocxTocItemRenderer(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Section.Type type) {
        return Section.Type.story.equals(type);
    }

    @Override
    public Stream<Object> render(Section source) {
        return Stream.of(docx.tocItem(Integer.toString(source.getPage()), source.getTitle()));
    }
}
