package net.kemitix.binder.app;

import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.File;

@ApplicationScoped
public class HtmlToEpubProducer {

    private final BinderConfig binderConfig;

    @Inject
    public HtmlToEpubProducer(BinderConfig binderConfig) {
        this.binderConfig = binderConfig;
    }

    @Produces
    @ApplicationScoped
    HtmlToEpub htmlToEpub() {
        return htmlFiles -> {
            EPUB epub = new EPUB();
            OPSPublication publication = new OPSPublication();
            htmlFiles.forEach(htmlFile -> {
                String name = htmlFile.getName();
                publication.addSource(name);
                publication.addItem(htmlFile);
                epub.add(publication);
            });
            File epubFile = binderConfig.getEpubFile();
            try {
                epub.pack(epubFile);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Error creating epub file %s: %s",
                        epubFile, e.getMessage()), e);
            }
        };
    }

}
