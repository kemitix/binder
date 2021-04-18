package net.kemitix.binder.spi;

import java.util.stream.Stream;

/**
 * Render a source into a stream with a context.
 *
 * @param <T> the type of the source
 * @param <O> the type of the output stream
 * @param <R> the type of the context
 */
public interface Renderer<T, O, R extends RenderHolder<?>> {
    boolean canHandle(Section section);

    Stream<O> render(T source, Context<R> context);
}
