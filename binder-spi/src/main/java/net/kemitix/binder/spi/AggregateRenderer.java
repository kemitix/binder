package net.kemitix.binder.spi;

import javax.enterprise.inject.Instance;

public interface AggregateRenderer<R extends Renderer<T, O, C>, T, O, C> {

    default R findRenderer(
            Section section,
            Instance<R> renderers
    ) {
        return renderers.stream()
                .filter(renderer -> renderer.canHandle(section))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported section type: %s"
                                .formatted(section.getType())));
    }

}
