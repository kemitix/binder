package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.markdown.DocumentNodeHandler;
import org.apache.commons.text.StringEscapeUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.stream.Stream;

@Epub
@ApplicationScoped
public class DocumentEpubNodeHandler
        implements DocumentNodeHandler<String, EpubRenderHolder>,
        EpubNodeHandler {

    private final String stylesheetHref;

    @Inject
    public DocumentEpubNodeHandler(
            @Named String stylesheetHref
    ) {
        this.stylesheetHref = stylesheetHref;
    }

    @Override
    public Stream<String> documentBody(
            String title,
            Stream<String> content
    ) {
        final String storyTitle = Objects.requireNonNullElse(title, "");
        return Stream.of(
                """
                        <?xml version='1.0' encoding='utf-8'?>
                        <!DOCTYPE html>
                        <html
                         xmlns="http://www.w3.org/1999/xhtml"
                         xmlns:epub="http://www.idpf.org/2007/ops">
                        <head>
                        <title>%s</title>
                        <link rel="stylesheet" href="%s" type="text/css" media="all"/>
                        </head>
                        <body>
                        <h1 style="text-align: center;">%s</h1>
                        %s
                        </body>
                        </html>
                        """.formatted(
                        storyTitle,
                        stylesheetHref,
                        storyTitle,
                        collect(content)));
    }

    @Override
    public Stream<String> documentStoryBody(
            String title,
            String author,
            Stream<String> content
    ) {
        final String storyTitle = StringEscapeUtils.escapeXml11(Objects.requireNonNull(title, "Story title"));
        return Stream.of(
                """
                        <?xml version='1.0' encoding='utf-8'?>
                        <!DOCTYPE html>
                        <html
                         xmlns="http://www.w3.org/1999/xhtml"
                         xmlns:epub="http://www.idpf.org/2007/ops">
                        <head>
                        <title>%s</title>
                        <link rel="stylesheet" href="%s" type="text/css" media="all"/>
                        </head>
                        <body>
                        <h1 style="text-align: center;">%s</h1>
                        <h2 style="text-align: center;">%s</h2>
                        %s
                        </body>
                        </html>
                        """.formatted(
                        storyTitle,
                        stylesheetHref,
                        storyTitle,
                        Objects.requireNonNull(author, "Story author"),
                        collect(content)));
    }
}
