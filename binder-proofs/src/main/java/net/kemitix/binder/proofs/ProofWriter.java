package net.kemitix.binder.proofs;

import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

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
        return doWrite().run(createEnv(log, binderConfig, proofs, metadata));
    }

    private static Reader<WriteProofEnv, ResultVoid> doWrite() {
        return env -> Result
                .ofVoid(() -> env.log().info("Writing proofs to: " + env.dir()))
                .andThen(() -> Result.applyOver(
                        env.proofs().stream(),
                        proofEnv -> {
                            final String title = Proof.getTitle().run(proofEnv);
                            env.log().info("Creating proof: " + title);
                            Proof.writeToFile(env.dir(), env.docx()).run(proofEnv);
                        }));
    }

    static WriteProofEnv createEnv(
            Logger log,
            BinderConfig binderConfig,
            Proofs proofs,
            Metadata metadata
    ) {
        return new WriteProofEnv() {
            @Override
            public Logger log() {
                return log;
            }

            @Override
            public String dir() {
                return binderConfig.getProofDir().getAbsolutePath();
            }

            @Override
            public Proofs proofs() {
                return proofs;
            }

            @Override
            public DocxFacade docx() {
                return new DocxFacade(metadata);
            }
        };
    }

    interface WriteProofEnv {

        Logger log();
        String dir();
        Proofs proofs();
        DocxFacade docx();
    }

}
