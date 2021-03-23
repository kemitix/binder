package net.kemitix.binder.spi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.enterprise.inject.Vetoed;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@With
@Vetoed
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlManuscript {

    private MdManuscript mdManuscript;
    private Map<Section.Name, String> htmlSections;

    public static HtmlManuscript.HtmlBuilder htmlBuilder() {
        return mdManuscript -> htmlSections ->
                new HtmlManuscript(mdManuscript, htmlSections);
    }

    public Metadata getMetadata() {
        return mdManuscript.getMetadata();
    }

    public List<Section> getContents() {
        return getMdManuscript().getContents();
    }

    public Stream<HtmlSection> sections() {
        return mdManuscript.getContents().stream()
                .map(section -> HtmlSection.create(section, htmlSections.get(section.getName())));
    }

    public interface HtmlBuilder {
        HtmlManuscript.HtmlBuilder.Stage1 metadata(MdManuscript mdManuscript);
        interface Stage1 {
            HtmlManuscript htmlSections(Map<Section.Name, String> htmlSections);
        }
    }

}
