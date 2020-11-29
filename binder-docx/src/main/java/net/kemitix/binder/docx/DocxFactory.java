package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Section;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class DocxFactory {

    @Produces
    @ApplicationScoped
    public List<DocxContent> create(
            MdManuscript mdManuscript,
            DocxMdRenderer docxMdRenderer
    ) {
        List<Section> sections = mdManuscript.getContents();
        if (sections.size() > 0) {
            Section lastSection = sections.get(sections.size() - 1);
            lastSection.setLast(true);
        }
        return sections
                .stream()
                .filter(Section::isDocx)
                .flatMap(docxMdRenderer::render)
                .collect(Collectors.toList());
    }

}
