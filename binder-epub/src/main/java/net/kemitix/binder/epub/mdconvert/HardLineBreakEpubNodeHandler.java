package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.HardLineBreakNodeHandler;
import net.kemitix.binder.spi.Context;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HardLineBreakEpubNodeHandler
        implements HardLineBreakNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    @Override
    public Stream<String> lineBreak(Context<EpubRenderHolder> context) {
        return Stream.of(
                "<br/>"
        );
    }

}
