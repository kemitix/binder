package net.kemitix.binder.spi;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TocSections {

    default Stream<HtmlSection> tocSections(
            HtmlManuscript htmlManuscript,
            Predicate<HtmlSection> predicate
    ) {
        return htmlManuscript.sections()
                .filter(Section::isToc)
                .filter(predicate)
                ;
    }

    Stream<HtmlSection> stories(HtmlManuscript htmlManuscript);

    default List<HtmlSection> stories(
            HtmlManuscript htmlManuscript,
            Predicate<HtmlSection> predicate,
            Section.Genre genre
    ) {
        return stories(htmlManuscript)
                .filter(isGenre(genre))
                .filter(predicate)
                .collect(Collectors.toList());
    }

    default List<HtmlSection> stories(
            HtmlManuscript htmlManuscript,
            Predicate<HtmlSection> predicate
    ) {
        return stories(htmlManuscript)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    default List<HtmlSection> stories(
            HtmlManuscript htmlManuscript,
            Section.Genre genre
    ) {
        return stories(htmlManuscript)
                .filter(isGenre(genre))
                .collect(Collectors.toList());
    }

    default Predicate<HtmlSection> isGenre(Section.Genre genre) {
        return htmlSection -> htmlSection.isGenre(genre);
    }

}
