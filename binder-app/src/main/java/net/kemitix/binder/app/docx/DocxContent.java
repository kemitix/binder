package net.kemitix.binder.app.docx;

import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class DocxContent {

    private Collection<?> contents = new ArrayList<>();

    public DocxContent(Collection<?> contents) {
        this.contents = contents;
    }

    public void save(String file) {
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii("Century Gothic");

        XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
        try {
            var wordMLPackage =
                    WordprocessingMLPackage.createPackage();
            var mainDocument =
                    wordMLPackage.getMainDocumentPart();
            var numberingDefinitionsPart = new NumberingDefinitionsPart();

            numberingDefinitionsPart.unmarshalDefaultNumbering();

            mainDocument.addTargetPart(numberingDefinitionsPart);
            mainDocument
                    .getContent()
                    .addAll(contents);
            wordMLPackage.save(new File(file));
        } catch (Docx4JException | JAXBException e) {
            throw new RuntimeException(
                    "Error saving file: %s".formatted(file), e);
        }
    }
}
