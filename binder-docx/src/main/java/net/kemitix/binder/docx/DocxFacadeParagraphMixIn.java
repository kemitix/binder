package net.kemitix.binder.docx;

import org.docx4j.wml.P;

public interface DocxFacadeParagraphMixIn
        extends DocxFacadeMixIn {

    P p();

    P zeroSpaceAfterP(P p);

    P styledP(String styleName, P p);

}
