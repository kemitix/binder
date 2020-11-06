package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import coza.opencollab.epub.creator.util.MediaTypeUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.IOException;

@ApplicationScoped
public class StylesheetProducer {

    @Produces
    @Named
    Content stylesheet(
            @Named String stylesheetHref
    ) throws IOException {
        String mediaType = "text/css";
        byte[] bytes = getClass()
                .getResourceAsStream("stylesheet.css")
                .readAllBytes();
        Content content = new Content(mediaType, "content/" + stylesheetHref, bytes);
        content.setToc(false);
        content.setSpine(false);
        return content;
    }

    @Produces
    @Named
    String stylesheetHref() {
        return "css/binder.css";
    }

}
