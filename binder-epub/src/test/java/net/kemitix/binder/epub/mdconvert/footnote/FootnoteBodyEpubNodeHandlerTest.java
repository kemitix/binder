package net.kemitix.binder.epub.mdconvert.footnote;

import net.kemitix.binder.epub.EpubRenderHolder;
import net.kemitix.binder.spi.Context;
import net.kemitix.binder.spi.Footnote;
import net.kemitix.binder.spi.Section;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FootnoteBodyEpubNodeHandlerTest
        implements WithAssertions {

    FootnoteStoreEpubProvider footnoteStoreEpubProvider = new FootnoteStoreEpubProvider();
    FootnoteBodyEpubNodeHandler sut = new FootnoteBodyEpubNodeHandler(footnoteStoreEpubProvider);
    @Mock Context<EpubRenderHolder> context;

    @BeforeEach
    void setUp() {
        given(context.getName()).willReturn(Section.name("test"));
    }

    @Test
    void doNotInsertOrdinalOnSimpleParagraph() {
        //given
        var content = Stream.of("<p>body text</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p>body text</p>");
    }

    @Test
    void doNotInsertOrdinalOnStyledParagraph() {
        //given
        var content = Stream.of("<p styled>body text</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p styled>body text</p>");
    }

    @Test
    void doNotinsertOrdinalOnFirstSimpleParagraph() {
        //given
        var content = Stream.of("<p>body text</p><p>para 2</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p>body text</p>\n<p>para 2</p>");
    }

    @Test
    void doNotInsertOrdinalOnFirstStyledParagraph() {
        //given
        var content = Stream.of("<p styled>body text</p><p>para 2</p>");
        //when
        var result = sut.footnoteBody(Footnote.ordinal("1"), content, context).collect(Collectors.joining());
        //then
        assertThat(result).isEqualTo("<p styled>body text</p>\n<p>para 2</p>");
    }

}