package net.kemitix.binder.app;

import javax.enterprise.inject.Instance;

public interface Renderer<T, O> {
    boolean canHandle(String type);

    O render(T source);
}
