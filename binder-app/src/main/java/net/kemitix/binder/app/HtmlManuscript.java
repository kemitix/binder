package net.kemitix.binder.app;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.enterprise.inject.Vetoed;
import java.util.Map;

@Getter
@With
@Vetoed
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlManuscript {

    private MdManuscript mdManuscript;
    private Map<String, String> htmlSections;

    static HtmlManuscript.HtmlBuilder htmlBuilder() {
        return mdManuscript -> htmlSections ->
                new HtmlManuscript(mdManuscript, htmlSections);
    }

    public Metadata getMetadata() {
        return mdManuscript.getMetadata();
    }

    public interface HtmlBuilder {
        HtmlManuscript.HtmlBuilder.Stage1 metadata(MdManuscript mdManuscript);
        interface Stage1 {
            HtmlManuscript htmlSections(Map<String, String> htmlSections);
        }
    }

}
