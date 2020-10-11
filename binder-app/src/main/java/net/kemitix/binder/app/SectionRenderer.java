package net.kemitix.binder.app;

public interface SectionRenderer<T, O> {
    boolean canHandle(String type);

    O render(T htmlSection);
}
