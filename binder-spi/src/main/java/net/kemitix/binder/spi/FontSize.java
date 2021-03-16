package net.kemitix.binder.spi;

import net.kemitix.mon.Wrapper;

public interface FontSize extends Wrapper<Integer> {

    static FontSize of(Integer fontSize) {
        return () -> fontSize;
    }

}
