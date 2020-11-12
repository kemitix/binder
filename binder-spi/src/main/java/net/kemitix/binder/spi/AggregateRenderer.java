package net.kemitix.binder.spi;

import javax.enterprise.inject.Instance;

public interface AggregateRenderer<R extends Renderer<T, O>, T, O> {

    default R findRenderer(
            Section.Type type,
            Instance<R> renderers
    ) {
        return renderers.stream()
                .filter(renderer -> renderer.canHandle(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported renderer: %s"
                                .formatted(type)));
    }

}
