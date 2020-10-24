package net.kemitix.binder.spi;

import java.util.List;

public interface TextImageFactory {

    List<TextImage> createImages(String text, FontSize fontSize, int pageWidth);

}
