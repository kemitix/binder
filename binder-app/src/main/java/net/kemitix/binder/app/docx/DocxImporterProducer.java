package net.kemitix.binder.app.docx;

import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DocxImporterProducer {

    @Produces
    @Dependent
    XHTMLImporterImpl xhtmlImporter() throws InvalidFormatException {
        WordprocessingMLPackage aPackage = WordprocessingMLPackage.createPackage();
        XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(aPackage);
        xhtmlImporter.setHyperlinkStyle("Hyperlink");
        return xhtmlImporter;
    }

}
