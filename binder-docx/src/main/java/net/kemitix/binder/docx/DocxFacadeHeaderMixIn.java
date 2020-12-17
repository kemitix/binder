package net.kemitix.binder.docx;

import lombok.SneakyThrows;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Hdr;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.docx4j.wml.P;
import org.docx4j.wml.SectPr;

import java.util.Arrays;
import java.util.List;

public interface DocxFacadeHeaderMixIn
        extends DocxFacadeParagraphMixIn {

    default void addBlankPageHeader(SectPr sectPr, String name) {
        P[] emptyP = new P[]{zeroSpaceAfterP(p()), p()};
        addPageHeader(sectPr, name, HdrFtrRef.DEFAULT, emptyP);
        addPageHeader(sectPr, name, HdrFtrRef.EVEN, emptyP);
        addPageHeader(sectPr, name, HdrFtrRef.FIRST, emptyP);
    }

    @SneakyThrows
    default void addDefaultPageHeader(
            SectPr sectPr,
            String name,
            P pageHeader
    ) {
        addPageHeader(sectPr, name, HdrFtrRef.DEFAULT, new P[]{
                zeroSpaceAfterP(pageHeader), p()
        });
    }

    @SneakyThrows
    default void addEvenPageHeader(
            SectPr sectPr,
            String name,
            P pageHeader
    ) {
        addPageHeader(sectPr, name, HdrFtrRef.EVEN, new P[]{
                zeroSpaceAfterP(pageHeader), p()
        });
    }

    @SneakyThrows
    default void addFirstPageHeader(
            SectPr sectPr,
            String name,
            P pageHeader
    ) {
        addPageHeader(sectPr, name, HdrFtrRef.FIRST, new P[]{
                zeroSpaceAfterP(pageHeader), p()
        });
    }

    @SneakyThrows
    default void addPageHeader(
            SectPr sectPr,
            String name,
            HdrFtrRef hdrFtrRef,
            P[] headerContent
    ) {
        HeaderPart headerPart = new HeaderPart();
        PartName partName = new PartName("/word/headers/%s/%s.xml".formatted(
                hdrFtrRef.value(), name));
        headerPart.setPartName(partName);
        Relationship relationship = mainDocumentPart().addTargetPart(headerPart);
        Hdr hdr = factory().createHdr();
        headerPart.setJaxbElement(hdr);
        List<Object> hdrContent = hdr.getContent();
        Arrays.stream(headerContent)
                .map(p -> styledP("Header", p))
                .forEach(hdrContent::add);
        HeaderReference headerReference = factory().createHeaderReference();
        headerReference.setId(relationship.getId());
        headerReference.setType(hdrFtrRef);
        sectPr.getEGHdrFtrReferences().add(headerReference);
    }
}
