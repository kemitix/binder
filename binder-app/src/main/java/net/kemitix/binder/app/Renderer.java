package net.kemitix.binder.app;

public interface Renderer<T, O> {
    boolean canHandle(String type);

    O render(T source);
}
