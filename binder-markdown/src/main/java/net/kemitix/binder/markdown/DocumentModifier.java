package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import net.kemitix.binder.spi.Context;

import java.util.function.BiFunction;

public interface DocumentModifier extends BiFunction<Document, Context, Document> {
}
