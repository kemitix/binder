package net.kemitix.binder.spi;

import net.kemitix.mon.result.Result;

public interface ManuscriptWriter {

    Result<Void> write();

}
