package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Log
@ApplicationScoped
public class ModifierFootnoteParagraphs
        implements DocumentModifier{

    NodeVisitor visitor = new NodeVisitor(
            new VisitHandler<>(FootnoteBlock.class, this::visit)
    );

    @Override
    public Document apply(Document document, Context context) {
        visitor.visit(document);
        return document;
    }

    private <N extends Node> void visit(@NotNull FootnoteBlock footnoteBlock) {
        BasedSequence ordinal = footnoteBlock.getText();
        Paragraph originalPara =
                Objects.requireNonNull((Paragraph) footnoteBlock.getFirstChild(), "Footnote first child");
        List<Paragraph> paras = splitParagraphs(originalPara);
        footnoteBlock.removeChildren();
        paras.forEach(footnoteBlock::appendChild);
    }

    private List<Paragraph> splitParagraphs(Paragraph paragraph) {
        List<Paragraph> paras = new ArrayList<>();
        AtomicInteger lineCounter = new AtomicInteger();
        paragraph.getContentLines().forEach(line -> {
            int currentLine = lineCounter.getAndIncrement();
            if (!line.toString().equals("~PARA~\n")) {
                Paragraph p = new Paragraph();
                p.setContent(paragraph, currentLine, currentLine + 1);
                BasedSequence contentChars = p.getContentChars().trim();
                p.setChars(contentChars);
                p.setTrailingBlankLine(true);
                p.setLineIndents(new int[]{1});
                Text t = new Text(contentChars);
                p.appendChild(t);
                paras.add(p);
            }
        });
        return paras;
    }
}
