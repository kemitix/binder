package net.kemitix.binder.proofs;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.stream.Stream;

@Named
@ApplicationScoped
public class Proofs {
    public Stream<Proof> stream() {
        return Stream.empty();//TODO
    }
}
