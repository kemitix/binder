package net.kemitix.binder.docx;

import lombok.SneakyThrows;
import net.kemitix.binder.spi.Section;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.Ftr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.P;
import org.docx4j.wml.SectPr;

import java.util.Arrays;
import java.util.List;

public interface DocxFacadeFooterMixIn
        extends DocxFacadeParagraphMixIn {
    
    default void addBlankPageFooter(SectPr sectPr, Section.Name name) {
        P[] emptyP = new P[]{zeroSpaceAfterP(p()), p()};
        addPageFooter(sectPr, name, HdrFtrRef.DEFAULT, emptyP);
        addPageFooter(sectPr, name, HdrFtrRef.EVEN, emptyP);
        addPageFooter(sectPr, name, HdrFtrRef.FIRST, emptyP);
    }

    @SneakyThrows
    default void addDefaultPageFooter(
            SectPr sectPr,
            Section.Name name,
            P pageFooter
    ) {
        addPageFooter(sectPr, name, HdrFtrRef.DEFAULT, new P[]{
                zeroSpaceAfterP(p()), pageFooter
        });
    }

    @SneakyThrows
    default void addEvenPageFooter(
            SectPr sectPr,
            Section.Name name,
            P pageFooter
    ) {
        addPageFooter(sectPr, name, HdrFtrRef.EVEN, new P[]{
                zeroSpaceAfterP(p()), pageFooter
        });
    }

    @SneakyThrows
    default void addFirstPageFooter(
            SectPr sectPr,
            Section.Name name,
            P pageFooter
    ) {
        addPageFooter(sectPr, name, HdrFtrRef.FIRST, new P[]{
                zeroSpaceAfterP(p()), pageFooter
        });
    }

    @SneakyThrows
    default void addPageFooter(
            SectPr sectPr,
            Section.Name name,
            HdrFtrRef hdrFtrRef,
            P[] footerContent
    ) {
        FooterPart footerPart = new FooterPart();
        PartName partName = new PartName("/word/footers/%s/%s.xml".formatted(
                hdrFtrRef.value(), name));
        footerPart.setPartName(partName);
        Relationship relationship = mainDocumentPart().addTargetPart(footerPart);
        Ftr ftr = factory().createFtr();
        footerPart.setJaxbElement(ftr);
        List<Object> ftrContent = ftr.getContent();
        Arrays.stream(footerContent)
                .map(p -> styledP("Header", p))
                .forEach(ftrContent::add);
        FooterReference footerReference = factory().createFooterReference();
        footerReference.setId(relationship.getId());
        footerReference.setType(hdrFtrRef);
        sectPr.getEGHdrFtrReferences().add(footerReference);
    }
}
