package net.kemitix.binder.docx;

import lombok.extern.java.Log;
import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Section;

import java.util.List;
import java.util.stream.Collectors;

@Log
public class DocxFactory {

    public List<DocxContent> create(
            MdManuscript mdManuscript,
            DocxMdRenderer docxMdRenderer,
            DocxRenderHolder docxRenderHolder
    ) {
        List<Section> sections = mdManuscript.getContents();
        if (sections.size() > 0) {
            Section lastSection = sections.get(sections.size() - 1);
            lastSection.setLast(true);
        }
        return sections
                .stream()
                .filter(Section::isDocx)
                .flatMap((Section section) -> docxMdRenderer.render(section, docxRenderHolder))
                .collect(Collectors.toList());
    }

}
