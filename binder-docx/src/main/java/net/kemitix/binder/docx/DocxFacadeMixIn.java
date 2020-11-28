package net.kemitix.binder.docx;

import org.docx4j.openpackaging.Base;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.ObjectFactory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DocxFacadeMixIn {

    ObjectFactory factory();

    Base mainDocumentPart();

    default <T, K> K get(
            T t,
            Function<T, K> extract,
            Consumer<K> apply,
            Supplier<K> create
    ) {
        K k = Objects.requireNonNullElseGet(
                extract.apply(t),
                create
        );
        apply.accept(k);
        return k;
    }

    default BooleanDefaultTrue defaultTrue() {
        return factory().createBooleanDefaultTrue();
    }

}
