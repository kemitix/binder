package net.kemitix.binder.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlSection {
    private final Section section;
    private final String html;

    public static HtmlSection create(Section section, String html) {
        return new HtmlSection(section, html);
    }

    public boolean isEpub() {
        return section.isEpub();
    }

    public String getType() {
        return section.getType();
    }

    public String getName() {
        return section.getName();
    }
}
