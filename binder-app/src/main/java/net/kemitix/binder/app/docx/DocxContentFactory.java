package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBException;

@Log
@ApplicationScoped
public class DocxContentFactory {

    public DocxContent create(String name, String html) {
        log.info(String.format("Created Content: %s", name));
        try {
            String baseURL = "BASEURL";
            RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
            rfonts.setAscii("Century Gothic");
            XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
            wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
            ndp.unmarshalDefaultNumbering();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
            XHTMLImporter.setHyperlinkStyle("Hyperlink");
            wordMLPackage.getMainDocumentPart().getContent().addAll(
                    XHTMLImporter.convert(html, baseURL));
            return new DocxContent(wordMLPackage);
        } catch (Docx4JException | JAXBException e) {
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(name), e);
        }
    }
}
