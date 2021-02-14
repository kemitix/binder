package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;

import java.util.function.BiFunction;

public interface DocumentModifier extends BiFunction<Document, Context, Document> {
}
