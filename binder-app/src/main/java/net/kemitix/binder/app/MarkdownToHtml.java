package net.kemitix.binder.app;

import net.kemitix.binder.spi.Section;

import java.util.function.Function;

public interface MarkdownToHtml extends Function<Section, String> {
}
