package net.kemitix.binder.app;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;

public class InstanceStream<T> implements Instance<T> {

    private final List<T> instances;

    public InstanceStream(List<T> instances) {
        this.instances = instances;
    }

    @Override
    public Instance<T> select(Annotation... qualifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U extends T> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <U extends T> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUnsatisfied() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAmbiguous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy(T instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return instances.iterator();
    }

    @Override
    public T get() {
        throw new UnsupportedOperationException();
    }
}
