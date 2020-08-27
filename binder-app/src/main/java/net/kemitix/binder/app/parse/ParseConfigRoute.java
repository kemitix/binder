package net.kemitix.binder.app.parse;

import lombok.extern.java.Log;
import net.kemitix.binder.app.ManuscriptConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.snakeyaml.SnakeYAMLDataFormat;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class ParseConfigRoute
        extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:Binder.ParseConfig")
                .routeId("Binder.ParseConfig")
                .unmarshal(new SnakeYAMLDataFormat(ManuscriptConfig.class))
                .process(exchange ->
                        log.info(exchange
                                .getIn()
                                .getBody(ManuscriptConfig.class)
                                .toString()))
        ;
    }
}
