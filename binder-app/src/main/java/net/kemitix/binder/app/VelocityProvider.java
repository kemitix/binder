package net.kemitix.binder.app;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Instant;

@ApplicationScoped
public class VelocityProvider {

    @Produces
    @ApplicationScoped
    VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader.file.path", "/");
        return velocityEngine;
    }

    @Produces
    @ApplicationScoped
    Context velocityContext() {
        Context context = new VelocityContext();
        context.put("timestamp", Instant.now().toString());
        return context;
    }

}
