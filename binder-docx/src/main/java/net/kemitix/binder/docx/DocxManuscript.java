package net.kemitix.binder.docx;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;
import net.kemitix.mon.result.VoidCallable;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.FontTablePart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.CTVerticalAlignRun;
import org.docx4j.wml.Fonts;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.Style;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Log
public class DocxManuscript {

    public static Reader<DocxManuscriptEnv, ResultVoid> writeToFile(
            String fileName,
            DocxFacade docx
    ) {
        return env -> Result.ok()
                .andThen(DocxManuscript::configureFontMapping)
                .inject(() -> new File(fileName))
                .peek(file -> env.log().info("Writing: " + file))
                .thenWith(file -> () -> Files.deleteIfExists(file.toPath()))
                .thenWith(file -> () -> createMainDocument(docx).run(env)
                        .flatMapV(mlPackage -> Result.ofVoid(() ->
                                mlPackage.save(file)))
                )
                .peek(file -> env.log().info("Wrote: " + file))
                .toVoid();
    }

    private static void configureFontMapping() {
        RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
        rfonts.setAscii("Century Gothic");
        XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
    }

    private static Reader<DocxManuscriptEnv, Result<WordprocessingMLPackage>> createMainDocument(
            DocxFacade docx
    ) {
        return env -> Result.of(docx::getMlPackage)
                .thenWith(wordMLPackage -> () -> {
                    var mainDocument = wordMLPackage.getMainDocumentPart();
                    mainDocument.addTargetPart(numberingDefinitionPart());
                    fontDefinitionsPart(mainDocument);

                    styleDefinitionsPart(mainDocument, docx).run(env);
                    enabledEvenAndOddHeaders(mainDocument).run(env);

                    mainDocument.getContent().addAll(getContents(docx).run(env));
                });
    }

    // headers: add "<w:evenAndOddHeaders/>" to settings.xml
    private static Reader<DocxManuscriptEnv, ResultVoid> enabledEvenAndOddHeaders(
            MainDocumentPart mainDocument
    ) {
        return env -> Result.ofVoid(() -> mainDocument
                .getDocumentSettingsPart()
                .getJaxbElement()
                .setEvenAndOddHeaders(env.factory().createBooleanDefaultTrue()));
    }

    private static ResultVoid fontDefinitionsPart(MainDocumentPart mainDocument) {
        return Result.ofVoid(() -> {
            FontTablePart part = new FontTablePart(new PartName("/word/fontTable.xml"));
            mainDocument.addTargetPart(part);
            Fonts defaultFonts = (Fonts) part.unmarshalDefaultFonts();
            part.setJaxbElement(defaultFonts);
        });
    }

    @SneakyThrows
    private static Reader<DocxManuscriptEnv, ResultVoid> styleDefinitionsPart(
            MainDocumentPart mainDocument,
            DocxFacade docx
    ) {
        return env -> Result.ofVoid(() -> {
            var part = mainDocument.getStyleDefinitionsPart(true);

            List<Style> styles = part.getContents().getStyle();

            styles.add(styleFootnote(docx).run(env));
            styles.add(styleFootnoteAnchor().run(env));
            styles.add(styleFootnoteCharacters(docx).run(env));

            RFonts rFonts = env.factory().createRFonts();
            rFonts.setAscii("Cambria");
            rFonts.setHAnsi("Cambria");

            RPr rpr = env.factory().createRPr();
            rpr.setRFonts(rFonts);

            String normalId = part.getIDForStyleName("Normal");
            Style normal = part.getStyleById(normalId);
            normal.setRPr(rpr);
        });
    }

    // The character that appears in the body of the text indicating
    // that there is a footnote.
    private static Reader<DocxManuscriptEnv, Style> styleFootnoteAnchor() {
        return env -> {
            Style style = env.factory().createStyle();

            style.setType("character");
            style.setStyleId("FootnoteAnchor");

            Style.Name name = new Style.Name();
            name.setVal("Footnote Anchor");
            style.setName(name);

            // Superscript
            RPr rPr = env.factory().createRPr();
            CTVerticalAlignRun ctVerticalAlignRun = env.factory().createCTVerticalAlignRun();
            ctVerticalAlignRun.setVal(STVerticalAlignRun.SUPERSCRIPT);
            rPr.setVertAlign(ctVerticalAlignRun);
            style.setRPr(rPr);

            return style;
        };
    }

    // The character within the footnote indicating the id of the
    // footnote. Matches the character that appears in the footnote
    // anchor.
    private static Reader<DocxManuscriptEnv, Style> styleFootnoteCharacters(DocxFacadeStyleMixIn docx) {
        return env -> {
            Style style = env.factory().createStyle();

            style.setType("character");
            style.setStyleId("FootnoteCharacters");

            Style.Name name = new Style.Name();
            name.setVal("Footnote Characters");
            style.setName(name);

            // Font size 11pt (i.e. 22 /2)
            RPr rPr = env.factory().createRPr();
            HpsMeasure sz = docx.sz(env.metadata().getPaperbackFootnoteFontSize());
            rPr.setSz(sz);
            rPr.setSzCs(sz);
            style.setRPr(rPr);

            // <w:qFormat/>
            style.setQFormat(env.factory().createBooleanDefaultTrue());

            return style;
        };
    }

    // The body of the footnote text.
    private static Reader<DocxManuscriptEnv, Style> styleFootnote(DocxFacade docx) {
        return env -> {
            Style style = env.factory().createStyle();

            style.setType("paragraph");
            style.setStyleId("Footnote");

            Style.BasedOn normal = new Style.BasedOn();
            normal.setVal("Normal");
            style.setBasedOn(normal);

            Style.Name name = new Style.Name();
            name.setVal("Footnote Text");
            style.setName(name);

            PPr pPr = env.factory().createPPr();

            pPr.setSuppressLineNumbers(env.factory().createBooleanDefaultTrue());

            // fully justified
            pPr.setJc(docx.jc(docx.alignmentBoth()));

            // indent margins
            PPrBase.Ind ind = env.factory().createPPrBaseInd();
            BigInteger margin = BigInteger.valueOf(400);
            ind.setLeft(margin);
            ind.setRight(margin);
            ind.setHanging(margin);
            pPr.setInd(ind);

            // do not put space between paragraphs of the same style
            PPrBase.Spacing spacing = env.factory().createPPrBaseSpacing();
            spacing.setBefore(BigInteger.ZERO);
            spacing.setAfter(BigInteger.valueOf(75));
            pPr.setSpacing(spacing);
//        pPr.setContextualSpacing(factory.createBooleanDefaultTrue());

            style.setPPr(pPr);

            // Font size 10pt (i.e. 20 /2)
            RPr rPr = env.factory().createRPr();
            HpsMeasure sz = docx.sz(env.metadata().getPaperbackFootnoteFontSize());
            rPr.setSz(sz);
            rPr.setSzCs(sz);
            style.setRPr(rPr);

            return style;
        };
    }

    private static NumberingDefinitionsPart numberingDefinitionPart()
            throws InvalidFormatException, JAXBException {
        var part = new NumberingDefinitionsPart();
        part.unmarshalDefaultNumbering();
        return part;
    }

    protected static Reader<DocxManuscriptEnv, Collection<?>> getContents(DocxFacade docx) {
        return env -> {
            var contents = new DocxFactory()
                    .create(env.mdManuscript(), env.docxMdRenderer(), DocxRenderHolder.create(docx));
            return contents.stream()
                    .flatMap(docxContent -> docxContent.getContents().stream())
                    .collect(Collectors.toList());
        };
    }

    public interface DocxManuscriptEnv {
        default Logger log() {
            return DocxManuscript.log;
        }
        Metadata metadata();
        ObjectFactory factory();
        MdManuscript mdManuscript();
        DocxMdRenderer docxMdRenderer();
    }

}
