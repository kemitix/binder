package net.kemitix.binder.proofs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import net.kemitix.binder.docx.DocxContent;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.docx.DocxFactory;
import net.kemitix.binder.docx.DocxMdRenderer;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.binder.spi.Section;
import org.docx4j.wml.ObjectFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Named
@ApplicationScoped
public class Proofs {

    private final Map<Section.Name, ManuscriptWithContents> contents;
    private final Metadata metadata;
    private final DocxMdRenderer docxMdRenderer;

    @Inject
    public Proofs(
            Metadata metadata,
            MdManuscript mdManuscript,
            DocxMdRenderer docxMdRenderer
    ) {
        this.metadata = metadata;
        this.docxMdRenderer = docxMdRenderer;

        contents = new HashMap<>();
        mdManuscript.getContents()
                .forEach(section -> {
                    var docx = new DocxFacade(metadata);// new for each section
                    MdManuscript manuscript = mdManuscript.withContents(Collections.singletonList(section));
                    var docxContents = new DocxFactory().create(manuscript, docxMdRenderer, () -> docx);
                    contents.put(section.getName(),
                            new ManuscriptWithContents()
                                    .withManuscript(manuscript)
                                    .withContent(docxContents.get(0)));
                });
    }

    public Stream<Proof.ProofEnv> stream() {
        var objectFactory = new ObjectFactory();
        return contents.values().stream()
                .map((ManuscriptWithContents content) ->
                        new Proof.ProofEnv() {
                            @Override
                            public DocxContent docxContent() {
                                return content.content;
                            }

                            @Override
                            public Metadata metadata() {
                                return metadata;
                            }

                            @Override
                            public ObjectFactory factory() {
                                return objectFactory;
                            }

                            @Override
                            public MdManuscript mdManuscript() {
                                return content.manuscript;
                            }

                            @Override
                            public DocxMdRenderer docxMdRenderer() {
                                return docxMdRenderer;
                            }
                        });
    }

    @With
    @AllArgsConstructor
    @NoArgsConstructor
    static class ManuscriptWithContents {
        MdManuscript manuscript;
        DocxContent content;
    }
}
