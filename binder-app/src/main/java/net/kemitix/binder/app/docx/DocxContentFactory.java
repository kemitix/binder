package net.kemitix.binder.app.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.app.Section;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Log
@ApplicationScoped
public class DocxContentFactory {

    public DocxContent create(Section section) {
        log.info(String.format("Created Content: %s",
                section.getHtmlFile().getName()));
        try {
            File htmlFile = section.getHtmlFile();
            String unescaped = Files.readString(htmlFile.toPath(), StandardCharsets.UTF_8);
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
                    XHTMLImporter.convert(unescaped, baseURL));
            return new DocxContent(wordMLPackage);
        } catch (Docx4JException | IOException | JAXBException e) {
            try {
                log.severe(Files.readString(section.getHtmlFile().toPath()));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            throw new RuntimeException(
                    "Error create docx from HTML file for: %s"
                            .formatted(section.getTitle()), e);
        }
    }
}
