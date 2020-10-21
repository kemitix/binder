package net.kemitix.binder.spi;

public interface TextImage {

    byte[] getBytes();

    int getWidth();

    TextImage withWidth(int maxWidth);
}
