package net.kemitix.binder.spi;

public interface Renderer<T, O> {
    boolean canHandle(String type);

    O render(T source);
}
