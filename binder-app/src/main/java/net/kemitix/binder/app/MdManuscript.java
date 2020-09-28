package net.kemitix.binder.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.enterprise.inject.Vetoed;
import java.util.List;

@Getter
@With
@Vetoed
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MdManuscript {

    private Metadata metadata;

    private List<Section> contents;

    static Builder builder() {
        return metadata -> contents -> new MdManuscript(metadata, contents);
    }

    public interface Builder {
        Stage1 metadata(Metadata metadata);
        interface Stage1 {
            MdManuscript contents(List<Section> sections);
        }
    }
}
