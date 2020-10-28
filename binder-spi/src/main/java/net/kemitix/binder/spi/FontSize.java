package net.kemitix.binder.spi;

import net.kemitix.mon.TypeAlias;

public class FontSize extends TypeAlias<Integer> {

    protected FontSize(Integer value) {
        super(value);
    }

    public static FontSize of(Integer fontSize) {
        return new FontSize(fontSize);
    }
}
