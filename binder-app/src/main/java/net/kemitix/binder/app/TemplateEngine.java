package net.kemitix.binder.app;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class TemplateEngine {

    public String resolve(
            String templateBody,
            Section section,
            Manuscript manuscript
    ) {

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader.file.path", "/");
        Context context = new VelocityContext();

        context.put("manuscript", manuscript);
        context.put("section", section);

        try {
            Path template = Files.createTempFile("template", ".vm");
            Files.writeString(template, templateBody);

            Path file = Files.createTempFile("template", ".html");
            Writer writer = new FileWriter(file.toFile());

            ve.mergeTemplate(template.toString(), "UTF-8", context, writer);
            writer.flush();
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException("Error creating rendered HTML file", e);
        }
    }
}
