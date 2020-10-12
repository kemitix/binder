package net.kemitix.binder.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlSection extends Section {

    @Delegate
    private final Section section;
    private final String html;

    public static HtmlSection create(Section section, String html) {
        return new HtmlSection(section, html);
    }

    public String getHref() {
        return "content/%s.html".formatted(getName());
    }
}
