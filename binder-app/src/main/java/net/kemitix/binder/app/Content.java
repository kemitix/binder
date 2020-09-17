package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;

/**
 * Part of the main body of the document.
 */
@Setter
@Getter
class Content extends Section {
    private String author;
    private int pageNumber;
}
