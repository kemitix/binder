package net.kemitix.binder.app;

import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Section;

import java.util.function.BiFunction;

public interface MarkdownToHtml extends BiFunction<Section, MdManuscript, String> {
}
