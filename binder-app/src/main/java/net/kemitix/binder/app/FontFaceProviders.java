package net.kemitix.binder.app;

import net.kemitix.binder.spi.BinderConfig;
import net.kemitix.binder.spi.CoverFont;
import net.kemitix.binder.spi.Metadata;
import net.kemitix.fontface.FontCache;
import net.kemitix.fontface.FontLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.net.URI;

@ApplicationScoped
public class FontFaceProviders {

    @Produces
    @ApplicationScoped
    public FontCache fontCache(FontLoader fontLoader) {
        return new FontCache(fontLoader);
    }

    @Produces
    @ApplicationScoped
    public FontLoader fontLoader() {
        return new FontLoader();
    }

    @Produces
    @CoverFont
    URI coverFont(
            BinderConfig binderConfig,
            Metadata metadata
    ) {
        return binderConfig.getScanDirectory().toPath()
                .resolve(metadata.getTitleFont().toPath())
                .toUri();
    }

}
