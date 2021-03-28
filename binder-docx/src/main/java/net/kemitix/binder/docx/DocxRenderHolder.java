package net.kemitix.binder.docx;

import net.kemitix.binder.spi.RenderHolder;

public interface DocxRenderHolder
        extends RenderHolder<DocxFacade> {
    static DocxRenderHolder create(DocxFacade docx) {
        return new DocxRenderHolder() {
            @Override
            public DocxFacade getRenderer() {
                return docx;
            }
        };
    }
}
