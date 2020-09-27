package net.kemitix.binder.app;

import org.apache.velocity.app.VelocityEngine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class VelocityProvider {

    @Produces
    VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader.file.path", "/");
        return velocityEngine;
    }
}
