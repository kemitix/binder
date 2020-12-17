package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.HardLineBreakNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class HardLineBreakEpubNodeHandler
        implements HardLineBreakNodeHandler<String> {

    @Override
    public Stream<String> lineBreak() {
        return Stream.of(
                "<br/>"
        );
    }

}
