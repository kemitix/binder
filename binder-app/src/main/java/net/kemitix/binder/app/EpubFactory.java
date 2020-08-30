package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Collectors;

@ApplicationScoped
public class EpubFactory {

    private final Manuscript manuscript;
    private final HtmlToEpub htmlToEpub;

    @Inject
    public EpubFactory(
            Manuscript manuscript,
            HtmlToEpub htmlToEpub
    ) {
        this.manuscript = manuscript;
        this.htmlToEpub = htmlToEpub;
    }

    public void create() {
        htmlToEpub.accept(
                manuscript.getContents().stream()
                        .map(Section::getHtmlFile)
                        .collect(Collectors.toList()));
    }
}
