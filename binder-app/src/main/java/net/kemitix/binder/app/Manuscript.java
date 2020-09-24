package net.kemitix.binder.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.enterprise.inject.Vetoed;
import java.util.List;

@Getter
@Vetoed
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Manuscript {

    private ManuscriptMetadata metadata;

    private List<Section> contents;

    static Builder builder() {
        return metadata -> contents -> new Manuscript(metadata, contents);
    }

    public interface Builder {
        Stage1 metadata(ManuscriptMetadata metadata);
        interface Stage1 {
            Manuscript contents(List<Section> sections);
        }
    }
}
