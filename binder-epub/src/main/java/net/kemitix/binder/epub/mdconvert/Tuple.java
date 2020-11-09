package net.kemitix.binder.epub.mdconvert;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Tuple<A, B> {
    private final A first;
    private final B second;

    public static <A, B> Tuple<A, B> of(A first, B second) {
        return new Tuple<>(first, second);
    }

    public static <A, B> Tuple<A, B> of(Map.Entry<A, B> entry) {
        return Tuple.of(entry.getKey(), entry.getValue());
    }

    public <C> Tuple<C, B> mapFirst(Function<? super A, ? extends C> fn) {
        return Tuple.of(fn.apply(first), second);
    }

    public <C> Tuple<C, B> mapFirst(BiFunction<? super A, ? super B, ? extends C> fn) {
        return Tuple.of(fn.apply(first, second), second);
    }

    public <C> Tuple<A, C> mapSecond(Function<? super B, ? extends C> fn) {
        return Tuple.of(first, fn.apply(second));
    }

    public <C> Tuple<A, C> mapSecond(BiFunction<? super A, ? super B, ? extends C> fn) {
        return Tuple.of(first, fn.apply(first, second));
    }
}
