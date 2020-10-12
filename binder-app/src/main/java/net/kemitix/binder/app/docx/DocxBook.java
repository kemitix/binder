package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class DocxBook {

    private final List<DocxContent> contents;

    @Inject
    public DocxBook(List<DocxContent> contents) {
        this.contents = contents;
    }

    public void writeToFile(String file) {
        log.info("Write: " + file);
        configureFontMapping();
        try {
            createMainDocument().save(new File(file));
        } catch (Docx4JException | JAXBException e) {
            throw new RuntimeException(
                    "Error saving file: %s".formatted(file), e);
        }
    }

    private void configureFontMapping() {
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii("Century Gothic");
        XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
    }

    private WordprocessingMLPackage createMainDocument()
            throws InvalidFormatException, JAXBException {
        var wordMLPackage = WordprocessingMLPackage.createPackage();
        var mainDocument = wordMLPackage.getMainDocumentPart();
        definePartNumbering(mainDocument);
        mainDocument.getContent().addAll(getContents());
        return wordMLPackage;
    }

    private void definePartNumbering(MainDocumentPart mainDocument)
            throws InvalidFormatException, JAXBException {
        var numberingDefinitionsPart = new NumberingDefinitionsPart();
        numberingDefinitionsPart.unmarshalDefaultNumbering();
        mainDocument.addTargetPart(numberingDefinitionsPart);
    }

    private List<?> getContents() {
        return contents.stream()
                .flatMap(docxContent -> docxContent.getContents().stream())
                .collect(Collectors.toList());
    }

}
