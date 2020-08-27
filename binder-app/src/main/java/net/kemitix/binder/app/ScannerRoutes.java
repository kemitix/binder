package net.kemitix.binder.app;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.apache.camel.builder.Builder.bean;

@ApplicationScoped
public class ScannerRoutes
        extends RouteBuilder {

    @Inject BinderConfig binderConfig;
    @Inject ConfigFileFilter configFileFilter;
    @Inject SourceFileFilter sourceFileFilter;

    @Override
    public void configure() {
        fromF("file://%s?noop=true", binderConfig.getScanDirectory())
                .routeId("Load Manuscript configuration")
                .filter(bean(configFileFilter, "test(${header.CamelFileName})"))
                .log("Config file: ${header.CamelFileName}")
                .to("direct:Binder.ParseConfig")
        ;

        fromF("file-watch://%s", binderConfig.getScanDirectory())
                .routeId("Monitor Manuscript")
                .filter(bean(sourceFileFilter, "test(${header.CamelFileName})"))
                .log("File event: ${header.CamelFileEventType}" +
                        " occurred on file ${header.CamelFileName}" +
                        " at ${header.CamelFileLastModified}")
        ;
    }
}
