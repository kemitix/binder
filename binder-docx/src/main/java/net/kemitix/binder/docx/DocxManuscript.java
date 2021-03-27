package net.kemitix.binder.docx;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.FontTablePart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class DocxManuscript {

    private final Metadata metadata;
    @Getter(AccessLevel.PROTECTED)
    private final MdManuscript mdManuscript;
    @Getter(AccessLevel.PROTECTED)
    private final DocxMdRenderer docxMdRenderer;
    private final ObjectFactory factory = new ObjectFactory();

    @Inject
    public DocxManuscript(
            Metadata metadata,
            MdManuscript mdManuscript,
            DocxMdRenderer docxMdRenderer
    ) {
        this.metadata = metadata;
        this.mdManuscript = mdManuscript;
        this.docxMdRenderer = docxMdRenderer;
    }

    public void writeToFile(String fileName, DocxFacade docx) {
        configureFontMapping();
        try {
            File file = new File(fileName);
            Files.deleteIfExists(file.toPath());
            createMainDocument(docx).save(file);
            log.info("Wrote: " + file);
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

    private WordprocessingMLPackage createMainDocument(DocxFacade docx)
            throws InvalidFormatException, JAXBException {
        var wordMLPackage = docx.getMlPackage();
        var mainDocument = wordMLPackage.getMainDocumentPart();
        mainDocument.addTargetPart(numberingDefinitionPart());
        fontDefinitionsPart(mainDocument);
        styleDefinitionsPart(mainDocument, docx);
        enabledEvenAndOddHeaders(mainDocument);
        mainDocument.getContent().addAll(getContents(docx));
        return wordMLPackage;
    }

    // headers: add "<w:evenAndOddHeaders/>" to settings.xml
    private void enabledEvenAndOddHeaders(MainDocumentPart mainDocument) {
        mainDocument
                .getDocumentSettingsPart()
                .getJaxbElement()
                .setEvenAndOddHeaders(factory.createBooleanDefaultTrue());
    }

    @SneakyThrows
    private void fontDefinitionsPart(MainDocumentPart mainDocument) {
        FontTablePart part = new FontTablePart(new PartName("/word/fontTable.xml"));
        mainDocument.addTargetPart(part);
        Fonts defaultFonts = (Fonts) part.unmarshalDefaultFonts();
        part.setJaxbElement(defaultFonts);
    }

    @SneakyThrows
    private void styleDefinitionsPart(MainDocumentPart mainDocument, DocxFacade docx) {
        var part = mainDocument.getStyleDefinitionsPart(true);

        List<Style> styles = part.getContents().getStyle();
        styles.add(styleFootnote(docx));
        styles.add(styleFootnoteAnchor());
        styles.add(styleFootnoteCharacters(docx));

        RFonts rFonts = factory.createRFonts();
        rFonts.setAscii("Cambria");
        rFonts.setHAnsi("Cambria");

        RPr rpr = factory.createRPr();
        rpr.setRFonts(rFonts);

        String normalId = part.getIDForStyleName("Normal");
        Style normal = part.getStyleById(normalId);
        normal.setRPr(rpr);
    }

    // The character that appears in the body of the text indicating
    // that there is a footnote.
    private Style styleFootnoteAnchor() {
        Style style = factory.createStyle();

        style.setType("character");
        style.setStyleId("FootnoteAnchor");

        Style.Name name = new Style.Name();
        name.setVal("Footnote Anchor");
        style.setName(name);

        // Superscript
        RPr rPr = factory.createRPr();
        CTVerticalAlignRun ctVerticalAlignRun = factory.createCTVerticalAlignRun();
        ctVerticalAlignRun.setVal(STVerticalAlignRun.SUPERSCRIPT);
        rPr.setVertAlign(ctVerticalAlignRun);
        style.setRPr(rPr);

        return style;
    }

    // The character within the footnote indicating the id of the
    // footnote. Matches the character that appears in the footnote
    // anchor.
    private Style styleFootnoteCharacters(DocxFacadeStyleMixIn docx) {
        Style style = factory.createStyle();

        style.setType("character");
        style.setStyleId("FootnoteCharacters");

        Style.Name name = new Style.Name();
        name.setVal("Footnote Characters");
        style.setName(name);

        // Font size 11pt (i.e. 22 /2)
        RPr rPr = factory.createRPr();
        HpsMeasure sz = docx.sz(metadata.getPaperbackFootnoteFontSize());
        rPr.setSz(sz);
        rPr.setSzCs(sz);
        style.setRPr(rPr);

        // <w:qFormat/>
        style.setQFormat(factory.createBooleanDefaultTrue());

        return style;
    }

    // The body of the footnote text.
    private Style styleFootnote(DocxFacade docx) {
        Style style = factory.createStyle();

        style.setType("paragraph");
        style.setStyleId("Footnote");

        Style.BasedOn normal = new Style.BasedOn();
        normal.setVal("Normal");
        style.setBasedOn(normal);

        Style.Name name = new Style.Name();
        name.setVal("Footnote Text");
        style.setName(name);

        PPr pPr = factory.createPPr();

        pPr.setSuppressLineNumbers(factory.createBooleanDefaultTrue());

        // fully justified
        pPr.setJc(docx.jc(docx.alignmentBoth()));

        // indent margins
        PPrBase.Ind ind = factory.createPPrBaseInd();
        BigInteger margin = BigInteger.valueOf(400);
        ind.setLeft(margin);
        ind.setRight(margin);
        ind.setHanging(margin);
        pPr.setInd(ind);

        // do not put space between paragraphs of the same style
        PPrBase.Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setBefore(BigInteger.ZERO);
        spacing.setAfter(BigInteger.valueOf(75));
        pPr.setSpacing(spacing);
//        pPr.setContextualSpacing(factory.createBooleanDefaultTrue());

        style.setPPr(pPr);

        // Font size 10pt (i.e. 20 /2)
        RPr rPr = factory.createRPr();
        HpsMeasure sz = docx.sz(metadata.getPaperbackFootnoteFontSize());
        rPr.setSz(sz);
        rPr.setSzCs(sz);
        style.setRPr(rPr);

        return style;
    }

    private NumberingDefinitionsPart numberingDefinitionPart()
            throws InvalidFormatException, JAXBException {
        var part = new NumberingDefinitionsPart();
        part.unmarshalDefaultNumbering();
        return part;
    }

    protected Collection<?> getContents(DocxFacade docx) {
        var contents = new DocxFactory()
                .create(mdManuscript, docxMdRenderer, () -> docx);
        return contents.stream()
                .flatMap(docxContent -> docxContent.getContents().stream())
                .collect(Collectors.toList());
    }

}
