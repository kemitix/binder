package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.enterprise.inject.Vetoed;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Vetoed
public class Manuscript {

    ManuscriptMetadata metadata;

    private List<Section> contents = new ArrayList<>();

}
