package net.kemitix.binder.spi;

import java.io.File;

public interface TextImage {

    String getWord();

    File getFile();

    byte[] getBytes();

    int getWidth();

    TextImage withWidth(int maxWidth);
}
