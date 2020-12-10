package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.DocumentNodeHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class DocumentEpubNodeHandler
        implements DocumentNodeHandler<String>, EpubNodeHandler {

    private final String stylesheetHref;

    @Inject
    public DocumentEpubNodeHandler(
            @Named String stylesheetHref) {
        this.stylesheetHref = stylesheetHref;
    }

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
                        <link rel="stylesheet" href="%s" type="text/css" media="all"/>
                        </head>
                        <body>
                        <h1>%s</h1>
                        %s
                        </body>
                        </html>
                        """.formatted(
                        stylesheetHref,
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
                        <link rel="stylesheet" href="%s" type="text/css" media="all"/>
                        </head>
                        <body>
                        <h1>%s</h1>
                        <h2>%s</h2>
                        %s
                        </body>
                        </html>
                        """.formatted(
                        stylesheetHref,
                        title,
                        author,
                        collect(content)));
    }
}
