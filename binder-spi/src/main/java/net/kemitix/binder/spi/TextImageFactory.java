package net.kemitix.binder.spi;

import java.util.List;

public interface TextImageFactory {

    List<TextImage> createImages(String text, FontSpec fontSpec);

}
