package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.util.ast.Document;
import net.kemitix.binder.markdown.DocumentNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class DocumentEpubNodeHandler
        implements DocumentNodeHandler<String>, EpubNodeHandler {
    @Override
    public Stream<String> documentBody(Document node, Stream<String> content) {
        return Stream.of(
                """
                        <html
                         xmlns="http://www.w3.org/1999/xhtml"
                         xmlns:epub="http://www.idpf.org/2007/ops">
                        <head>
                        </head>
                        <body>
                        %s
                        </body>
                        </html>
                        """.formatted(collect(content)));
    }
}
