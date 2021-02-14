package net.kemitix.binder.markdown;

import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log
@ApplicationScoped
public class ModifierFootnoteParagraphs
        implements DocumentModifier{

    NodeVisitor findFootnotes = new NodeVisitor(
            new VisitHandler<>(FootnoteBlock.class, this::visitFootnoteBlock)
    );

    @Override
    public Document apply(Document document, Context context) {
        findFootnotes.visit(document);
        return document;
    }

    private <N extends Node> void visitFootnoteBlock(@NotNull FootnoteBlock footnoteBlock) {
        Paragraph originalPara =
                Objects.requireNonNull((Paragraph) footnoteBlock.getFirstChild(), "Footnote first child");
        if (originalPara.getChars().toString().contains("~PARA~")) {
            List<Paragraph> paras = new ReflowParagraphs(originalPara).reflow();
            footnoteBlock.removeChildren();
            paras.forEach(footnoteBlock::appendChild);
        }
    }

    @RequiredArgsConstructor
    private static class ReflowParagraphs {

        private final Paragraph paragraph;

        public List<Paragraph> reflow() {
            List<Paragraph> paras = new ArrayList<>();
            Optional.ofNullable(paragraph.getFirstChild())
                    .map(List::of)
                    .map(this::paraFrom)
                    .ifPresent(paras::add);
            List<Node> nextPara = new ArrayList<>();
            paragraph.getChildren().forEach(child -> {
                if (child instanceof SoftLineBreak) {
                    if (!nextPara.isEmpty()) {
                        paras.add(paraFrom(nextPara));
                    }
                    nextPara.clear();
                } else if (child instanceof Text && ((Text) child).getChars().toString().equals("~PARA~")) {
                    // skip
                } else {
                    nextPara.add(child);
                }
            });
            return paras;
        }

        private Paragraph paraFrom(List<Node> nodes) {
            Paragraph p = new Paragraph();
            nodes.forEach(p::appendChild);
            return p;
        }
    }


}
