package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FootnoteImpl<T, P>
        implements Footnote<T, P> {

    private final String ordinal;
    private final P placeholder;
    private final List<T> content;

}
