package net.kemitix.binder.proofs;

import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;

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
    public ResultVoid write() {
        return Result.ok(binderConfig.getProofDir())
                .map(File::getAbsolutePath)
                .peek(proofDir -> log.info("Writing proofs to: " + proofDir))
                .flatMap(this::writeProofs)
                .peek(x -> log.info("Wrote proofs"));
    }

    private ResultVoid writeProofs(String proofDir) {
        return Result.applyOver(
                proofs.stream(),
                proof -> {
                    log.info("Creating proof: " + proof.getTitle());
                    var docx = new DocxFacade(metadata);
                    proof.writeToFile(proofDir, docx);
                });
    }

}
