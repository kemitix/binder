package net.kemitix.binder.app;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemplateEngine {

    private static final String NON_BREAKING_SPACE = "\u00A0";
    private final VelocityEngine velocityEngine;

    @Inject
    public TemplateEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String resolve(
            String templateBody,
            Section section,
            MdManuscript mdManuscript
    ) {
        Context context = new VelocityContext();
        context.put("m", mdManuscript.getMetadata());
        context.put("c", mdManuscript.getContents());
        context.put("s", section);
        context.put("copyrights", copyrights(mdManuscript));
        Writer writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "", templateBody);
        return writer.toString();
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
