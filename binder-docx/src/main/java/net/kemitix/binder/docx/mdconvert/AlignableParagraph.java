package net.kemitix.binder.docx.mdconvert;

import net.kemitix.binder.docx.DocxFacade;
import net.kemitix.binder.spi.Context;
import org.docx4j.wml.P;

import java.util.stream.Stream;

public interface AlignableParagraph {

    default Stream<Object> alignP(
            P p,
            DocxFacade docx,
            Context context
    ) {
        P alignedP = switch (context.getAlign()) {
            case right -> docx.alignRight(p);
            case full -> docx.alignFull(p);
            case left -> docx.alignLeft(p);
            case centre -> docx.alignCenter(p);
        };
        return Stream.of(alignedP);
    }

}
