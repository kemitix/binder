package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ext.footnotes.Footnote;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kemitix.binder.spi.Section;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FootnoteAnchor {
    private final String storeName;
    private final String oridinal;

    public static FootnoteAnchor create(
            Footnote footnote,
            Section section
    ) {
        String storeName = section.getName() + "-footnotes.html";
        String ordinal = footnote.getText().unescape();
        return new FootnoteAnchor(storeName, ordinal);
    }

}
