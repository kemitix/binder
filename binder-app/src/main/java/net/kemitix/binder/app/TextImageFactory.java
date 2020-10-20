package net.kemitix.binder.app;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class TextImageFactory {
    public List<TextImage> createImages(String text, int fontSize) {
        //TODO create text image files
        return Collections.emptyList();
    }
}
