package net.kemitix.binder.app;

import net.kemitix.binder.spi.TextImage;
import net.kemitix.binder.spi.TextImageFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class DefaultTextImageFactory
        implements TextImageFactory {
    @Override
    public List<TextImage> createImages(String text, int fontSize) {
        //TODO
        return Collections.emptyList();
    }
}
