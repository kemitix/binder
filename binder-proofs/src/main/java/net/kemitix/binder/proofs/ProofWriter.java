package net.kemitix.binder.proofs;

import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Writes the proof files to disk
 */
@Log
@ApplicationScoped
public class ProofWriter implements ManuscriptWriter {

    private final BinderConfig binderConfig;
    private final Proofs proofs;
    private final Metadata metadata;

    @Inject
    public ProofWriter(
            BinderConfig binderConfig,
            Proofs proofs,
            Metadata metadata
    ) {
        this.binderConfig = binderConfig;
        this.proofs = proofs;
        this.metadata = metadata;
    }

    @Override
    public void write() {
        var proofDir = binderConfig.getProofDir().getAbsolutePath();
        log.info("Writing proofs to: " + proofDir);
        proofs.stream()
                .forEach(proof -> {
                    var docx = new DocxFacade(metadata);
                    proof.writeToFile(proofDir, docx);
                });
        log.info("Wrote proofs");
    }
}
