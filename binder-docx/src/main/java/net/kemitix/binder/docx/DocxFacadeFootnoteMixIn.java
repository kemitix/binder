package net.kemitix.binder.docx;

import lombok.SneakyThrows;
import net.kemitix.binder.docx.mdconvert.footnote.DocxFootnote;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.ManuscriptFormatException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STFtnEdn;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DocxFacadeFootnoteMixIn
        extends DocxFacadeParagraphMixIn {

    AtomicInteger footnoteRef();

    /**
     * Creates a Footnote with no body.
     *
     * <p>Use {@link #footnoteAddBody(DocxFootnote.Placeholder, Stream)} to add
     * the body.</p>
     *
     * @param ordinal the ordinal of the footnote
     * @return an R containing the footnote anchor as a subscript
     */
    default R footnote(Footnote.Ordinal ordinal) {
        return footnoteReference(ordinal);
    }

    @SneakyThrows
    default R footnoteReference(Footnote.Ordinal ordinal) {
        // in document.xml:
        //      <w:r>
        //        <w:rPr>
        //          <w:rStyle w:val="FootnoteAnchor"/>
        //        </w:rPr>
        //        <w:footnoteReference w:id="2"/>
        //      </w:r>
        FootnotesPart footnotesPart = getFootnotesPart();
        CTFootnotes contents = footnotesPart.getContents();
        List<CTFtnEdn> footnotes = contents.getFootnote();
        CTFtnEdn ctFtnEdn = getNextCtFtnEdn(footnotes);
//        //ctFtnEdn.getContent().addAll(Arrays.asList(footnoteBody(footnoteBody)));
        CTFtnEdnRef ctFtnEdnRef = factory().createCTFtnEdnRef();
        BigInteger id = ctFtnEdn.getId();
        ctFtnEdnRef.setId(id);
        JAXBElement<CTFtnEdnRef> footnoteReference = factory().createRFootnoteReference(ctFtnEdnRef);

        Footnote.Ordinal footnoteOrdinal = Footnote.ordinal(id.toString());
        if (!footnoteOrdinal.equals(ordinal)) {
            throw new RuntimeException(
                    "Footnotes are not in order. Found %s, when expecting %s"
                            .formatted(ordinal, footnoteOrdinal));
        }

        R r = r(new Object[]{
                footnoteReference
        });

        RPr rPr = rPr(r);
        rPr.setRStyle(rStyle("FootnoteAnchor"));

        return r;
    }

    @SneakyThrows
    default void footnoteAddBody(
            DocxFootnote.Placeholder placeholder,
            Stream<P> content
    ) {
        FootnotesPart footnotesPart = getFootnotesPart();
        CTFootnotes contents = footnotesPart.getContents();
        List<CTFtnEdn> footnotes = contents.getFootnote();
        List<Object> rContent = placeholder.getValue().getContent();
        JAXBElement<CTFtnEdnRef> footnoteReference = (JAXBElement<CTFtnEdnRef>) rContent.get(0);
        CTFtnEdnRef ctFtnEdnRef = footnoteReference.getValue();
        BigInteger id = ctFtnEdnRef.getId();
        CTFtnEdn ctFtnEdn = getCtFtnEdnById(footnotes, id);
        List<Object> ctFtnEdnContent = ctFtnEdn.getContent();
        List<Object> paras = Arrays.asList(footnoteBody(content.collect(Collectors.toList())));
        ctFtnEdnContent.addAll(paras);
    }

    default CTFtnEdn getCtFtnEdnById(List<CTFtnEdn> footnotes, BigInteger id) {
        return footnotes.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }

    default CTFtnEdn getNextCtFtnEdn(List<CTFtnEdn> footnotes) {
        var id = BigInteger.valueOf(footnoteRef().getAndIncrement());
        return footnotes.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    CTFtnEdn edn = factory().createCTFtnEdn();
                    edn.setId(id);
                    footnotes.add(edn);
                    return edn;
                });
    }

    default FootnotesPart getFootnotesPart() {
        return Objects.requireNonNullElseGet(
                mainDocumentPart().getFootnotesPart(),
                () -> {
                    try {
                        FootnotesPart part = new FootnotesPart();
                        part.setContents(initFootnotes());
                        mainDocumentPart().addTargetPart(part);
                        return part;
                    } catch (InvalidFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    default CTFootnotes initFootnotes() {
        CTFootnotes ctFootnotes = factory().createCTFootnotes();
        List<CTFtnEdn> footnotes = ctFootnotes.getFootnote();

        //    <w:footnote w:id="-1" w:type="separator">
        //        <w:p>
        //            <w:r>
        //                <w:separator/>
        //            </w:r>
        //        </w:p>
        //    </w:footnote>
        CTFtnEdn separator = getNextCtFtnEdn(footnotes);
        separator.setType(STFtnEdn.SEPARATOR);
        separator.setId(BigInteger.valueOf(-1));
        separator.getContent().add(
                p(
                        r(
                                factory().createRSeparator()
                        )
                )
        );

        //    <w:footnote w:id="0" w:type="continuationSeparator">
        //        <w:p>
        //            <w:r>
        //                <w:continuationSeparator/>
        //            </w:r>
        //        </w:p>
        //    </w:footnote>
        CTFtnEdn continuation = getNextCtFtnEdn(footnotes);
        continuation.setType(STFtnEdn.CONTINUATION_SEPARATOR);
        continuation.setId(BigInteger.ZERO);
        continuation.getContent().add(
                p(
                        r(
                                factory().createRContinuationSeparator()
                        )
                )
        );

        return ctFootnotes;
    }

    default Object[] footnoteBody(List<P> footnoteParas) {
        // in footnotes.xml:
        //  <w:footnote w:id="2">
        //    <w:p>
        //      <w:pPr>
        //        <w:pStyle w:val="Footnote"/>
        //        <w:rPr/>
        //      </w:pPr>
        //      <w:r>
        //        <w:rPr>
        //          <w:rStyle w:val="FootnoteCharacters"/>
        //        </w:rPr>
        //        <w:footnoteRef/>
        //      </w:r>
        //      <w:r>
        //        <w:rPr/>
        //        <w:tab/>
        //        <w:t>Footnote</w:t>
        //      </w:r>
        //    </w:p>
        //  </w:footnote>
        PPr pPr = pPr();
        pPr.setPStyle(pStyle("Footnote"));

        List<Object> objects = new ArrayList<>();
        objects.add(pPr);
        R r = r(
                factory().createRFootnoteRef()
        );
        RPr rPr = rPr(r);
        rPr.setRStyle(rStyle("FootnoteCharacters"));

        List<P> paras = footnoteParas.stream()
                .peek(para -> para.setPPr(pPr)
                ).collect(Collectors.toList());
        objects.addAll(paras);
        P firstP = paras.get(0);
        firstP.getContent().add(0, r);
        return objects.toArray();
    }
}
