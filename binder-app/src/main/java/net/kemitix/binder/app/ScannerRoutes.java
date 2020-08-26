package net.kemitix.binder.app;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ScannerRoutes
        extends RouteBuilder {

    @Inject BinderConfig binderConfig;

    @Override
    public void configure() {
        fromF("file-watch://%s", binderConfig.getScanDirectory())
                .routeId("Scan Directory")
                .log("File event: ${header.CamelFileEventType}" +
                        " occurred on file ${header.CamelFileName}" +
                        " at ${header.CamelFileLastModified}");
        ;
    }
}
