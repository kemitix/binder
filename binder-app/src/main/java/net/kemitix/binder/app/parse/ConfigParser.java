package net.kemitix.binder.app.parse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import net.kemitix.binder.app.ManuscriptConfig;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigParser {

    private static final ObjectReader OBJECT_READER =
            new ObjectMapper()
                    .reader()
                    .forType(new TypeReference<ManuscriptConfig>() {});

    ManuscriptConfig parse(String body)
            throws JsonProcessingException
    {
        return OBJECT_READER.readValue(body);
    }

}
