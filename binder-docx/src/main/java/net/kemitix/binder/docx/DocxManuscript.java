package net.kemitix.binder.docx;

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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Log
@ApplicationScoped
public class DocxManuscript {

    private final List<Object> contents = new ArrayList<>();

    private final DocxFacade docx;

    @Inject
    public DocxManuscript(DocxFacade docx) {
        this.docx = docx;
    }

    public void writeToFile(String fileName) {
        log.info("Write: " + fileName);
        configureFontMapping();
        try {
            File file = new File(fileName);
            Files.deleteIfExists(file.toPath());
            createMainDocument().save(file);
        } catch (Docx4JException | JAXBException | IOException e) {
            throw new RuntimeException(
                    "Error saving file: %s".formatted(fileName), e);
        }
    }

    private void configureFontMapping() {
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii("Century Gothic");
        XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
    }

    private WordprocessingMLPackage createMainDocument()
            throws InvalidFormatException, JAXBException {
        var wordMLPackage = docx.getMlPackage();
        var mainDocument = wordMLPackage.getMainDocumentPart();
        definePartNumbering(mainDocument);
        mainDocument.getContent().addAll(contents);
        return wordMLPackage;
    }

    private void definePartNumbering(MainDocumentPart mainDocument)
            throws InvalidFormatException, JAXBException {
        var numberingDefinitionsPart = new NumberingDefinitionsPart();
        numberingDefinitionsPart.unmarshalDefaultNumbering();
        mainDocument.addTargetPart(numberingDefinitionsPart);
    }

}
