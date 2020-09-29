package net.kemitix.binder.app;

import lombok.extern.java.Log;
import net.kemitix.binder.app.docx.DocxFactory;
import net.kemitix.binder.app.docx.DocxWriter;
import net.kemitix.binder.app.epub.EpubContentFactory;
import net.kemitix.binder.app.epub.EpubFactory;
import net.kemitix.binder.app.epub.EpubWriter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Log
@ApplicationScoped
public class BinderApp {

    private final HtmlFactory htmlFactory;
    private final EpubFactory epubFactory;
    private final EpubWriter epubWriter;
    private final DocxWriter docxWriter;
    private final DocxFactory docxFactory;

    @Inject
    public BinderApp(
            HtmlFactory htmlFactory,
            EpubFactory epubFactory,
            EpubWriter epubWriter,
            DocxWriter docxWriter,
            DocxFactory docxFactory
    ) {
        this.htmlFactory = htmlFactory;
        this.epubFactory = epubFactory;
        this.epubWriter = epubWriter;
        this.docxWriter = docxWriter;
        this.docxFactory = docxFactory;
    }

    public void run(String[] args) {
        log.info("Binder - Starting");
        epubWriter.write();
        docxWriter.write();
        log.info("Binder - Done.");
    }

}
