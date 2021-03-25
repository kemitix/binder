package net.kemitix.binder.proofs;

import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.spi.Metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Stream;

@Named
@ApplicationScoped
public class Proofs {

    private final List<DocxContent> contents;
    private final DocxFacade docx;
    private final Metadata metadata;

    @Inject
    public Proofs(
            List<DocxContent> contents,
            DocxFacade docx,
            Metadata metadata
    ) {
        this.contents = contents;
        this.docx = docx;
        this.metadata = metadata;
    }

    public Stream<Proof> stream() {
        return contents.stream()
                .map(docxContent -> new Proof(docxContent, docx, metadata));
    }
}
