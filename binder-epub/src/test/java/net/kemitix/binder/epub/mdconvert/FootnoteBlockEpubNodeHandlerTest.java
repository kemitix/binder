package net.kemitix.binder.epub.mdconvert;

import com.vladsch.flexmark.ext.footnotes.FootnoteBlock;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import net.kemitix.binder.markdown.FootnoteBody;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

class FootnoteBlockEpubNodeHandlerTest
        implements WithAssertions {

    FootnoteBlockEpubNodeHandler sut = new FootnoteBlockEpubNodeHandler();
    FootnoteBlock block = new FootnoteBlock();

    @BeforeEach
    void setUp() {
        block.setText(BasedSequence.of("1"));
    }

    @Test void insertsOrdinalOnSimpleParagraph() {
        //given
        var in = FootnoteBody.create(block, Stream.of("<p>body text</p>"));
        //when
        var result = sut.footnoteBlockBody(in).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p><sup>1</sup>body text</p>");
    }

    @Test void insertsOrdinalOnStyledParagraph() {
        //given
        var in = FootnoteBody.create(block, Stream.of("<p styled>body text</p>"));
        //when
        var result = sut.footnoteBlockBody(in).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p styled><sup>1</sup>body text</p>");
    }

    @Test void insertsOrdinalOnFirstSimpleParagraph() {
        //given
        var in = FootnoteBody.create(block, Stream.of("<p>body text</p><p>para 2</p>"));
        //when
        var result = sut.footnoteBlockBody(in).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p><sup>1</sup>body text</p>\n<p>para 2</p>");
    }

    @Test void insertsOrdinalOnFirstStyledParagraph() {
        //given
        var in = FootnoteBody.create(block, Stream.of("<p styled>body text</p><p>para 2</p>"));
        //when
        var result = sut.footnoteBlockBody(in).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p styled><sup>1</sup>body text</p>\n<p>para 2</p>");
    }

}