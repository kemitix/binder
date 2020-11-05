package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.TextNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class TextEpubNodeHandler
        implements TextNodeHandler<String>, EpubNodeHandler {

    @Override
    public Stream<String> textBody(String text) {
        return Stream.of(
                "<span>%s</span>".formatted(text)
        );
    }

}
