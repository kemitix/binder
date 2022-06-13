package net.kemitix.binder.app;

import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Section;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemplateEngine {

    private static final String NON_BREAKING_SPACE = "\u00A0";
    private static final String DOUBLE_HASH = "##";
    private static final String NOT_A_COMMENT = "X-not-a-comment-X";

    private final VelocityEngine velocityEngine;
    private final Context velocityContext;

    @Inject
    public TemplateEngine(
            VelocityEngine velocityEngine,
            Context velocityContext
    ) {
        this.velocityEngine = velocityEngine;
        this.velocityContext = velocityContext;
    }

    public String resolve(
            String templateBody,
            Section section,
            MdManuscript mdManuscript) {
        velocityContext.put("s", section);
        velocityContext.put("m", mdManuscript.getMetadata());
        velocityContext.put("c", mdManuscript.getContents());
        velocityContext.put("copyrights", encode(copyrights(mdManuscript)));
        velocityContext.put("toc", toc(mdManuscript));
        Writer writer = new StringWriter();
        // Double hashes are comments in velocity, but at the start of a line
        // they are level two headers in markdown.
        // This style of comment is not supported, and are broken
        velocityEngine.evaluate(velocityContext, writer, "",
                templateBody.replaceAll(DOUBLE_HASH, NOT_A_COMMENT));
        return writer.toString()
                .replaceAll(NOT_A_COMMENT, DOUBLE_HASH);
    }

    private String toc(MdManuscript mdManuscript) {
        return mdManuscript.getContents().stream()
                .filter(Section::isToc)
                .map(tocEntry())
                .collect(Collectors.joining("<br/>"));
    }

    private Function<Section, String> tocEntry() {
        return section -> {
            if (section.isType(Section.Type.story)) {
                // entry with page number
                return "%s\t%s".formatted(
                        section.getPage(),
                        section.getTitle());
            } else {
                return "\t%s".formatted(
                        section.getTitle());
            }
        };
    }

    private String copyrights(MdManuscript mdManuscript) {
        return mdManuscript.getContents().stream()
                .filter(Section.Type.story::isA)
                .map(section -> "*%s* ©%s by%s%s".formatted(
                        section.getTitle(),
                        section.getCopyright(),
                        NON_BREAKING_SPACE,
                        unBreakable(section.getAuthor())
                ))
                .collect(Collectors.joining("  \n"));
    }

    String encode(String in) {
        return StringEscapeUtils.escapeXml11(in);
    }

    private String unBreakable(String s) {
        return s.replaceAll(" ", NON_BREAKING_SPACE);
    }

}
