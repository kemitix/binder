package net.kemitix.binder.app;

import net.kemitix.binder.spi.MdManuscript;
import net.kemitix.binder.spi.Section;
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

    private final VelocityEngine velocityEngine;
    private final Context context;

    @Inject
    public TemplateEngine(
            VelocityEngine velocityEngine,
            Context context
    ) {
        this.velocityEngine = velocityEngine;
        this.context = context;
    }

    public String resolve(
            String templateBody,
            Section section,
            MdManuscript mdManuscript) {
        context.put("s", section);
        context.put("m", mdManuscript.getMetadata());
        context.put("c", mdManuscript.getContents());
        context.put("copyrights", copyrights(mdManuscript));
        context.put("toc", toc(mdManuscript));
        Writer writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "", templateBody);
        return writer.toString();
    }

    private String toc(MdManuscript mdManuscript) {
        return mdManuscript.getContents().stream()
                .filter(Section::isToc)
                .map(tocEntry())
                .collect(Collectors.joining("<br/>"));
    }

    private Function<Section, String> tocEntry() {
        return section -> {
            if ("story".equals(section.getType())) {
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
                .filter(section -> "story".equals(section.getType()))
                .map(section -> "%s ©%s by%s%s".formatted(
                        section.getTitle(),
                        section.getCopyright(),
                        NON_BREAKING_SPACE,
                        unBreakable(section.getAuthor())
                ))
                .collect(Collectors.joining("<br/>"));
    }

    private String unBreakable(String s) {
        return s.replaceAll(" ", NON_BREAKING_SPACE);
    }

}
