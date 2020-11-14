package net.kemitix.binder.markdown;

import net.kemitix.binder.spi.Section;

import java.util.Objects;

public interface Context {
    static Context create(Section section) {
        return new Context() {
            @Override
            public boolean isType(Section.Type type) {
                return section.isType(type);
            }

            @Override
            public String getTitle() {
                return Objects.requireNonNullElse(
                        section.getTitle(),
                        "");
            }

            @Override
            public String getAuthor() {
                return Objects.requireNonNull(
                        section.getAuthor(),
                        "No author for " + section.getName());
            }

            @Override
            public String getName() {
                return section.getName();
            }
            //TODO access values in Section as required
        };
    }

    boolean isType(Section.Type type);

    String getTitle();

    String getAuthor();

    String getName();
}
