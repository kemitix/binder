package net.kemitix.binder.proofs;

import lombok.extern.java.Log;
import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.ManuscriptWriter;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.mon.reader.Reader;
import net.kemitix.mon.result.Result;
import net.kemitix.mon.result.ResultVoid;
import net.kemitix.mon.result.VoidCallable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.util.function.Function;
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
        DocxFacade docx = new DocxFacade(metadata);
        return doWrite().run(new WriteProofEnv(){
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
                return docx;
            }
        });
    }

    private static Reader<WriteProofEnv, ResultVoid> doWrite() {
        return env -> Result
                .ofVoid(() -> env.log().info("Writing proofs to: " + env.dir()))
                .recover(e -> Result.applyOver(
                        env.proofs().stream(),
                        proof -> {
                            env.log().info("Creating proof: " + proof.getTitle());
                            proof.writeToFile(env.dir(), env.docx());
                        }));
    }

    interface WriteProofEnv {
        Logger log();
        String dir();
        Proofs proofs();
        DocxFacade docx();
    }

}
