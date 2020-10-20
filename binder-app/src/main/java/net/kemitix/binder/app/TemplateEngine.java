package net.kemitix.binder.app;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.StringWriter;
import java.io.Writer;

@ApplicationScoped
public class TemplateEngine {

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
            Section section
    ) {
        context.put("s", section);
        Writer writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "", templateBody);
        return writer.toString();
    }

}
