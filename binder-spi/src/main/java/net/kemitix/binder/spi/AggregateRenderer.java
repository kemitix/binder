package net.kemitix.binder.spi;

import javax.enterprise.inject.Instance;

/**
 *
 * @param <R> The type of the renderer
 * @param <T> the type of the source
 * @param <O> the type of the output stream
 * @param <C> the type of the context
 */
public interface AggregateRenderer<R extends Renderer<T, O, C>, T, O, C extends RenderHolder<?>> {

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
