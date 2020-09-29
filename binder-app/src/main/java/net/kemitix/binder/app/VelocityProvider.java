package net.kemitix.binder.app;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Instant;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class VelocityProvider {

    private static final String NON_BREAKING_SPACE = "\u00A0";

    @Produces
    @ApplicationScoped
    VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader.file.path", "/");
        return velocityEngine;
    }

    @Produces
    @ApplicationScoped
    Context context(MdManuscript mdManuscript) {
        Context context = new VelocityContext();
        context.put("timestamp", Instant.now().toString());
        context.put("m", mdManuscript.getMetadata());
        context.put("c", mdManuscript.getContents());
        context.put("copyrights", copyrights(mdManuscript));
        context.put("toc", toc(mdManuscript));
        return context;
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
