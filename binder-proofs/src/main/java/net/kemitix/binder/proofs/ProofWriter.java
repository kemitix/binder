package net.kemitix.binder.proofs;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;

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

    @Inject
    public ProofWriter(
            BinderConfig binderConfig,
            Proofs proofs
    ) {
        this.binderConfig = binderConfig;
        this.proofs = proofs;
    }

    @Override
    public void write() {
        var proofDir = binderConfig.getProofDir().getAbsolutePath();
        log.info("Writing proofs to: " + proofDir);
        proofs.stream()
                .forEach(proof -> proof.writeToDir(proofDir));
        log.info("Wrote proofs");
    }
}
