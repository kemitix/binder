package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Part of the main body of the document.
 */
@Setter
@Getter
@ToString(callSuper = true)
class Content extends Section {
    private String author;
    private int page;
}
