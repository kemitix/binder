package net.kemitix.binder.epub;

import net.kemitix.binder.spi.RenderHolder;

public interface EpubRenderHolder
        extends RenderHolder<Void> {
    static EpubRenderHolder create() {
        return new EpubRenderHolder() {
            @Override
            public Void getRenderer() {
                return null;
            }
        };
    }
}
