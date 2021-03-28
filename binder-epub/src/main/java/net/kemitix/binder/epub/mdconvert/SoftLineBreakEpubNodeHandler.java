package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.SoftLineBreakNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class SoftLineBreakEpubNodeHandler
        implements SoftLineBreakNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {
    @Override
    public Stream<String> softLineBreakBody(Context<EpubRenderHolder> context) {
        return Stream.of(
                " "
        );
    }
}
