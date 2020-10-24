package net.kemitix.binder.spi;

public interface TextImage {

    String getWord();

    byte[] getBytes();

    int getWidth();

    TextImage withWidth(int maxWidth);
}
