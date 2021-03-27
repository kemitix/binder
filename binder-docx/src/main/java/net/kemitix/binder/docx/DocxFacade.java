package net.kemitix.binder.docx;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.SectPr;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

import static org.docx4j.jaxb.Context.getWmlObjectFactory;

public class DocxFacade
        implements DocxFacadeSectionMixIn,
        DocxFacadeStyleMixIn,
        DocxFacadeFootnoteMixIn {

    private final Metadata metadata;

    @Getter
    private final WordprocessingMLPackage mlPackage;

    private final ObjectFactory factory = getWmlObjectFactory();
    private final AtomicInteger myFootnoteRef = new AtomicInteger(-1);

    @SneakyThrows
    public DocxFacade(
            Metadata metadata
    )  {
        this.metadata = metadata;

        mlPackage = WordprocessingMLPackage.createPackage();
        SectPr sectPr = mlPackage.getDocumentModel().getSections().get(0).getSectPr();
        sectPr.setPgSz(pgSz());
        sectPr.setPgMar(pgMar());
        addBlankPageHeader(sectPr, Section.name("default"));
        addBlankPageFooter(sectPr, Section.name("default"));
        sectPrType("oddPage", sectPr);
    }

    @Override
    public ObjectFactory factory() {
        return factory;
    }

    @Override
    public WordprocessingMLPackage mlPackage() {
        return mlPackage;
    }

    @Override
    public AtomicInteger footnoteRef() {
        return myFootnoteRef;
    }

    @Override
    public MainDocumentPart mainDocumentPart() {
        return mlPackage.getMainDocumentPart();
    }

    @Override
    public Metadata metadata() {
        return this.metadata;
    }

}
