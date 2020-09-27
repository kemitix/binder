package net.kemitix.binder.app;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.StringWriter;
import java.io.Writer;

@ApplicationScoped
public class TemplateEngine {

    private final VelocityEngine velocityEngine;

    @Inject
    public TemplateEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String resolve(
            String templateBody,
            Section section,
            Manuscript manuscript
    ) {
        Context context = new VelocityContext();
        context.put("m", manuscript.getMetadata());
        context.put("c", manuscript.getContents());
        context.put("s", section);
        Writer writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "", templateBody);
        return writer.toString();
    }

}
