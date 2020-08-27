package net.kemitix.binder.app;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

@ApplicationScoped
public class ManuscriptLoader {

    @Produces
    ManuscriptMetadata manuscriptMetadata(BinderConfig binderConfig) throws IOException {
        Yaml yaml = new Yaml(new Constructor(ManuscriptMetadata.class));
        File configFile = getConfigFile(binderConfig);
        FileReader fileReader = new FileReader(configFile);
        return yaml.load(fileReader);
    }

    private File getConfigFile(BinderConfig binderConfig) {
        String scanDirectory = binderConfig.getScanDirectory();
        String userHome = System.getProperty("user.home");
        String fullPath = scanDirectory.replaceFirst("^~", userHome);
        return Paths.get(fullPath).resolve("binder.yaml")
                .toFile();
    }

    @Produces
    Manuscript manuscript(ManuscriptMetadata manuscriptMetaData) {
        Manuscript manuscript = new Manuscript();
        manuscript.setMetadata(manuscriptMetaData);
        //TODO - load preludes
        //TODO - load sections
        //TODO - load codas
        return manuscript;
    }

}
