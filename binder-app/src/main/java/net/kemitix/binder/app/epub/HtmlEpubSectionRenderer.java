package net.kemitix.binder.app.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.app.HtmlSection;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class HtmlEpubSectionRenderer
        extends EpubSectionRenderer<HtmlSection> {

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public Content render(HtmlSection htmlSection) {
        String html = htmlSection.getHtml();
        String name = htmlSection.getName();
        String href = "content/%s.html".formatted(name);
        return htmlContent(name, href, html);
    }
}
