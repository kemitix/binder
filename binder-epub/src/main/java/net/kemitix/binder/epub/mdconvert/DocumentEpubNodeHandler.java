package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Document;
import net.kemitix.binder.markdown.DocumentNodeHandler;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class DocumentEpubNodeHandler
        implements DocumentNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> documentBody(
            String title,
            Stream<String> content
    ) {
        return Stream.of(
                """
                        <html
                         xmlns="http://www.w3.org/1999/xhtml"
                         xmlns:epub="http://www.idpf.org/2007/ops">
                        <head>
                        </head>
                        <body>
                        <h1>%s</h1>
                        %s
                        </body>
                        </html>
                        """.formatted(
                        title,
                        collect(content)));
    }

    @Override
    public Stream<String> documentStoryBody(
            String title,
            String author,
            Stream<String> content
    ) {
        return Stream.of(
                """
                        <html
                         xmlns="http://www.w3.org/1999/xhtml"
                         xmlns:epub="http://www.idpf.org/2007/ops">
                        <head>
                        </head>
                        <body>
                        <h1>%s</h1>
                        <h2>%s</h2>
                        %s
                        </body>
                        </html>
                        """.formatted(
                        title,
                        author,
                        collect(content)));
    }
}
