package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.SoftLineBreakNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class SoftLineBreakEpubNodeHandler
        implements SoftLineBreakNodeHandler<String> {
    @Override
    public Stream<String> softLineBreakBody() {
        return Stream.of(
                "<br/>"
        );
    }
}
