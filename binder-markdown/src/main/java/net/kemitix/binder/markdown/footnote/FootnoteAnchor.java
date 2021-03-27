package net.kemitix.binder.markdown.footnote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.Footnote.Ordinal;
import net.kemitix.binder.spi.Section;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FootnoteAnchor {
    private final Section.Name name;
    private final Ordinal ordinal;
    private final String htmlFile;

    public static FootnoteAnchor create(
            com.vladsch.flexmark.ext.footnotes.Footnote footnote,
            Context context
    ) {
        var name = context.getName();
        var ordinal = Footnote.ordinal(footnote.getText().unescape());
        var htmlFile = "footnotes/%s/footnote-%s.xhtml"
                .formatted(name, ordinal);
        return new FootnoteAnchor(name, ordinal, htmlFile);
    }

}
