package net.kemitix.binder.app.docx;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ObjectFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class DocxProducers {

    @Produces
    ObjectFactory objectFactory() {
        return Context.getWmlObjectFactory();
    }

    @Produces
    @ApplicationScoped
    WordprocessingMLPackage mlPackage() throws InvalidFormatException {
        return WordprocessingMLPackage.createPackage();
    }

}
