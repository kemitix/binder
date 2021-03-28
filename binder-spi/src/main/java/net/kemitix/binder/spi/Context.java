package net.kemitix.binder.spi;

import java.util.Objects;

public interface Context<R extends RenderHolder<?>> {
    static <R extends RenderHolder<?>> Context<R> create(Section section, R renderer) {
        return new Context<>() {

            @Override
            public String toString() {
                return "Context: (type: %s, title: %s)"
                        .formatted(section.getType(), section.getTitle());
            }

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
            public Section.Name getName() {
                return section.getName();
            }

            @Override
            public Section.Align getAlign() {
                return section.getAlign();
            }

            @Override
            public float getFontSize() {
                return section.getFontSize();
            }

            @Override
            public String getParaStyleName() {
                return "section-%s-para".formatted(section.getName());
            }

            @Override
            public boolean hasHeader() {
                return section.isHeader();
            }

            @Override
            public boolean hasFooter() {
                return section.isFooter();
            }

            @Override
            public boolean isLastSection() {
                return section.isLast();
            }

            @Override
            public R getRendererHolder() {
                return renderer;
            }

            @Override
            public boolean startOnOddPage() {
                return section.startOnOddPage();
            }

            //TODO access values in Section as required
        };
    }

    static <R extends RenderHolder<?>> Context<R> create(R renderer) {
        return new Context<>() {

            @Override
            public String toString() {
                return "Context: No Section";
            }

            @Override
            public boolean isType(Section.Type type) {
                return false;
            }

            @Override
            public String getTitle() {
                return "";
            }

            @Override
            public String getAuthor() {
                return "";
            }

            @Override
            public Section.Name getName() {
                return Section.name("");
            }

            @Override
            public Section.Align getAlign() {
                return Section.Align.full;
            }

            @Override
            public float getFontSize() {
                return 0;
            }

            @Override
            public String getParaStyleName() {
                return "Normal";
            }

            @Override
            public boolean hasHeader() {
                return false;
            }

            @Override
            public boolean hasFooter() {
                return false;
            }

            @Override
            public boolean isLastSection() {
                return false;
            }

            @Override
            public R getRendererHolder() {
                return renderer;
            }

            @Override
            public boolean startOnOddPage() {
                return false;
            }
        };
    }

    boolean isType(Section.Type type);

    String getTitle();

    String getAuthor();

    Section.Name getName();

    Section.Align getAlign();

    float getFontSize();

    String getParaStyleName();

    boolean hasHeader();

    boolean hasFooter();

    boolean isLastSection();

    R getRendererHolder();

    boolean startOnOddPage();
}
