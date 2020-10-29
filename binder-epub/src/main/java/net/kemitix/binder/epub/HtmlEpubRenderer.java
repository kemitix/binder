package net.kemitix.binder.epub;

import coza.opencollab.epub.creator.model.Content;
import lombok.extern.java.Log;
import net.kemitix.binder.spi.HtmlSection;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class HtmlEpubRenderer
        implements EpubRenderer {

    @Override
    public boolean canHandle(String type) {
        return "html".equals(type)
                || "story".equals(type);
    }

    @Override
    public Content render(HtmlSection htmlSection) {
        String html = """
                    <html><head><title>%s</title></head>
                    <body>
                    %s

                    %s
                    </body>
                    </html>""".formatted(
                htmlSection.getTitle(),
                title(htmlSection),
                htmlSection.getHtml());
        String name = htmlSection.getName();
        String href = "content/%s.html".formatted(name);
        return htmlContent(href, html);
    }

    private String title(HtmlSection htmlSection) {
        if ("story".equals(htmlSection.getType())) {
            return """
                    <h2>%s</h2>
                    <h3>%s</h3>
                    """.formatted(
                    htmlSection.getTitle(),
                    htmlSection.getAuthor());
        }
        if (htmlSection.getTitle() == null) {
            return "";
        }
        return "<h2>%s</h2>".formatted(htmlSection.getTitle());
    }
}
