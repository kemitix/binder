package net.kemitix.binder.docx;

import net.kemitix.binder.docx.mdconvert.Docx;
import net.kemitix.binder.markdown.Context;
import net.kemitix.binder.markdown.MarkdownConverter;
import net.kemitix.binder.spi.FontSize;
import net.kemitix.binder.spi.Section;
import org.docx4j.wml.P;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class StoryDocxRenderer
        implements DocxRenderer {

    private final DocxFacade docx;
    private final DocxImageFacade docxImage;
    private final MarkdownConverter<Object> converter;

    @Inject
    public StoryDocxRenderer(
            DocxFacade docx,
            DocxImageFacade docxImage,
            @Docx MarkdownConverter<Object> converter
    ) {
        this.docx = docx;
        this.docxImage = docxImage;
        this.converter = converter;
    }

    @Override
    public boolean canHandle(Section section) {
        return section.isType(Section.Type.story);
    }

    @Override
    public Stream<DocxContent> render(Section section) {
        List<Object> contents = new ArrayList<>();
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.addAll(title(section));
        contents.add(docx.textParagraphCentered(section.getAuthor()));
        contents.add(docx.textParagraph(""));
        contents.add(docx.textParagraph(""));
        contents.addAll(converter.convert(
                Context.create(section),
                section.getMarkdown()
        ).collect(Collectors.toList()));
        contents.addAll(aboutAuthor(section));
        Context context = Context.create(section);
        docx.addStyle(docx.paraStyle(context));
        return Stream.of(
                new DocxContent(
                        docx.finaliseSection(context, contents))
        );
    }

    private Collection<?> aboutAuthor(Section section) {
        Object[] convert = converter.convert(Context.create(), section.getBio()).toArray();
        if (convert.length > 1) {
            throw new RuntimeException("More than one paragraph in Author Bio");
        }
        if (!(convert[0] instanceof P)) {
            throw new RuntimeException("Author Bio markdown should be a paragraph");
        }
        P authorBio = (P) convert[0];
        return Arrays.asList(
                docx.keepWithNext(docx.p()),
                docx.keepWithNext(docx.textParagraphCentered(
                        "© %4d %s".formatted(
                                section.getCopyright(), section.getAuthor()
                        ))),
                docx.keepWithNext(docx.p()),
                // TODO: history - if present
                docx.keepWithNext(docx.textParagraphCentered("About the Author")),
                docx.keepTogether(authorBio)
        );
    }

    private List<Object> title(Section sec) {
        String title = Objects.requireNonNullElse(sec.getTitle(), "");
        if (title.length() > 0) {
            return Arrays.asList(
                    docx.textParagraph(""),
                    docx.drawings(
                            docxImage.textImages(
                                    title,
                                    FontSize.of(240))),
                    docx.textParagraph("")
            );
        }
        return Collections.emptyList();
    }

}
