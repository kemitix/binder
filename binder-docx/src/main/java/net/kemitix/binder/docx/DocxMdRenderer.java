package net.kemitix.binder.docx;

import net.kemitix.binder.spi.AggregateRenderer;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.stream.Stream;

@ApplicationScoped
public class DocxMdRenderer
        implements AggregateRenderer<DocxRenderer, Section, DocxContent> {

    private final Instance<DocxRenderer> renderers;

    @Inject
    public DocxMdRenderer(Instance<DocxRenderer> renderers) {
        this.renderers = renderers;
    }

    public Stream<DocxContent> render(Section section) {
        return findRenderer(section.getType(), renderers)
                .render(section);
    }

}
