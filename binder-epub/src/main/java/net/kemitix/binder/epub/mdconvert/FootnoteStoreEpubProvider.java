package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.spi.FootnoteStore;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class FootnoteStoreEpubProvider {

    @Epub
    @Produces
    public FootnoteStore<String> footnoteStore() {
        return FootnoteStore.create(String.class);
    }

}
