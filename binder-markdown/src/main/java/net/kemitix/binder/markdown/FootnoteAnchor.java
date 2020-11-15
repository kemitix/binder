package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.Footnote;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FootnoteAnchor {
    private final String name;
    private final String oridinal;
    private final String htmlFile;

    public static FootnoteAnchor create(
            Footnote footnote,
            Context context
    ) {
        String name = context.getName();
        String ordinal = footnote.getText().unescape();
        String htmlFile = "../footnotes/" + name + "/footnote-%s.html"
                .formatted(ordinal);
        return new FootnoteAnchor(name, ordinal, htmlFile);
    }

}
