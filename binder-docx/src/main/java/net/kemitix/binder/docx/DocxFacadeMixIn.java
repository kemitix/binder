package net.kemitix.binder.docx;

import org.docx4j.openpackaging.Base;
import org.docx4j.wml.ObjectFactory;

public interface DocxFacadeMixIn {

    ObjectFactory factory();

    Base mainDocumentPart();
}
