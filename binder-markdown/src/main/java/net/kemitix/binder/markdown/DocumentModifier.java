package net.kemitix.binder.markdown;

import com.vladsch.flexmark.util.ast.Document;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.RenderHolder;

import java.util.function.BiFunction;

public interface DocumentModifier
        extends BiFunction<Document, Context<? extends RenderHolder<?>>, Document> {
}
