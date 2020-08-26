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
        fromF("file://%s?noop=true", binderConfig.getScanDirectory())
                .routeId("Load Manuscript")
                .log("File found: ${header.CamelFileName}")
        ;

        fromF("file-watch://%s", binderConfig.getScanDirectory())
                .routeId("Monitor Manuscript")
                .log("File event: ${header.CamelFileEventType}" +
                        " occurred on file ${header.CamelFileName}" +
                        " at ${header.CamelFileLastModified}");
        ;
    }
}
