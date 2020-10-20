package net.kemitix.binder.app;

import net.kemitix.binder.spi.ManuscriptWriter;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.enterprise.inject.Instance;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BinderTest
        implements WithAssertions {

    @Mock Instance<ManuscriptWriter> writers;
    @Mock ManuscriptWriter epubWriter;
    @Mock ManuscriptWriter docxWriter;

    @BeforeEach
    public void setUp() {
        given(writers.stream()).willReturn(Stream.of(epubWriter, docxWriter));
    }

    @Test
    void writesEpub() {
        //given
        var binderApp = new BinderApp(writers);
        //when
        binderApp.run(new String[] {});
        //then
        verify(epubWriter).write();
    }

    @Test
    void writesDocx() {
        //given
        var binderApp = new BinderApp(writers);
        //when
        binderApp.run(new String[] {});
        //then
        verify(docxWriter).write();
    }

}
