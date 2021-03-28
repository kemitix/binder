package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.Section;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class FootnoteBodyEpubNodeHandlerTest
        implements WithAssertions {

    FootnoteStoreEpubProvider footnoteStoreEpubProvider = new FootnoteStoreEpubProvider();
    FootnoteBodyEpubNodeHandler sut = new FootnoteBodyEpubNodeHandler(footnoteStoreEpubProvider);
    Context context = mock(Context.class);

    @BeforeEach
    void setUp() {
        given(context.getName()).willReturn(Section.name("test"));
    }

    @Test void insertsOrdinalOnSimpleParagraph() {
        //given
        var content = Stream.of("<p>body text</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p><sup>1</sup>body text</p>");
    }

    @Test void insertsOrdinalOnStyledParagraph() {
        //given
        var content = Stream.of("<p styled>body text</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p styled><sup>1</sup>body text</p>");
    }

    @Test void insertsOrdinalOnFirstSimpleParagraph() {
        //given
        var content = Stream.of("<p>body text</p><p>para 2</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p><sup>1</sup>body text</p>\n<p>para 2</p>");
    }

    @Test void insertsOrdinalOnFirstStyledParagraph() {
        //given
        var content = Stream.of("<p styled>body text</p><p>para 2</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p styled><sup>1</sup>body text</p>\n<p>para 2</p>");
    }

}