package net.kemitix.binder.docx;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class MarkdownDocxConverter {

    private final Parser parser;

    @Inject
    public MarkdownDocxConverter(Parser parser) {
        this.parser = parser;
    }

    public List<Object> convert(String markdown) {
        Document document = parser.parse(markdown);
        //TODO
        return Collections.emptyList();
    }

}
