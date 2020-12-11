package net.kemitix.binder.epub.mdconvert;

import net.kemitix.binder.markdown.Context;

import java.util.stream.Stream;

public interface AlignableParagraph {

    default Stream<String> align(
            String element,
            String text,
            Context context
    ) {
        String alignment = switch (context.getAlign()) {
            case left -> "left";
            case right -> "right";
            case full -> "justify";
            case centre -> "center";
        };
        return Stream.of(
                """
                        <%1$s style="text-align: %2$s">%3$s</%1$s>
                        """
                        .formatted(element, alignment, text.strip())
        );
    }

}
