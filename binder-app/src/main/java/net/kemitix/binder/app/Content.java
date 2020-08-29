package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
class Content extends Section {
    private String author;
    private int page;
}
