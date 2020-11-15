package net.kemitix.binder.docx;

import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class DefaultDocxTocItemRenderer
        implements DocxTocItemRenderer {

    private final DocxFacade docx;

    @Inject
    public DefaultDocxTocItemRenderer(DocxFacade docx) {
        this.docx = docx;
    }

    @Override
    public boolean canHandle(Section section) {
        // anything except story
        return !section.isType(Section.Type.story);
    }

    @Override
    public Stream<Object> render(Section source) {
        return Stream.of(docx.tocItem("", source.getTitle()));
    }
}
