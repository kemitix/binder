package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.spi.FootnoteStore;
import org.docx4j.wml.P;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class FootnoteStoreDocxProvider {

    @Docx
    @Produces
    public FootnoteStore<P> footnoteStore() {
        return FootnoteStore.create(P.class);
    }

}
