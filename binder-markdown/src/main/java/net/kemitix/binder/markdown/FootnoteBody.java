package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FootnoteBody<T> {

    private final String oridinal;
    private final Stream<T> content;

    public static <T> FootnoteBody<T> create(
            FootnoteBlock footnoteBlock,
            Stream<T> content
    ) {
        String oridinal = footnoteBlock.getText().unescape();
        return new FootnoteBody<>(oridinal, content);
    }

}
