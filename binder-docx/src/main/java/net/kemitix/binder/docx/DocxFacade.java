package net.kemitix.binder.docx;

import lombok.Getter;
import net.kemitix.binder.spi.Metadata;
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

@ApplicationScoped
public class DocxFacade
        implements DocxFacadeSectionMixIn,
        DocxFacadeStyleMixIn,
        DocxFacadeFootnoteMixIn {

    private final Metadata metadata;

    @Getter
    private final WordprocessingMLPackage mlPackage;

    private final ObjectFactory factory = getWmlObjectFactory();
    private final AtomicInteger myFootnoteRef = new AtomicInteger(0);

    @Inject
    public DocxFacade(
            Metadata metadata
    ) throws InvalidFormatException {
        this.metadata = metadata;

        mlPackage = WordprocessingMLPackage.createPackage();
        SectPr sectPr = mlPackage.getDocumentModel().getSections().get(0).getSectPr();
        sectPr.setPgSz(pgSz());
        sectPr.setPgMar(pgMar());
        addBlankPageHeader(sectPr, "default");
        addBlankPageFooter(sectPr, "default");
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
