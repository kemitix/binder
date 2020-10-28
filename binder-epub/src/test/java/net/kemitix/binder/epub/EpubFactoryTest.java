package net.kemitix.binder.epub;

import org.assertj.core.api.WithAssertions;

public class EpubFactoryTest
        implements WithAssertions {

    private final ObjectMother o = new ObjectMother();

//    @Test
//    public void shouldErrorWhenScanDirectoryDoesNotExist() {
//        given(o.binderConfig().getScanDirectory())
//                .willReturn(new File("does not exist"));
//        assertThatCode(() -> o.epubFactory().create())
//                .isInstanceOf(MissingBinderDirectory.class);
//    }
//
//    @Test
//    public void shouldErrorWhenScanDirectoryHasNoConfigFile() {
//        given(o.binderConfig().getScanDirectory())
//                .willReturn(new File(getClass().getResource("../invalid").getPath()));
//        assertThatCode(() -> o.epubFactory().create())
//                .isInstanceOf(ManuscriptFormatException.class);
//    }
//
//    @Nested
//    public class ValidBinderDirectory {
//
//        EpubBook epubBook;
//
//        @BeforeEach
//        public void setUp() {
//            given(o.binderConfig().getScanDirectory())
//                    .willReturn(new File(getClass().getResource("../valid").getPath()));
//            epubBook = o.epubFactory().create();
//        }
//
//        @Test
//        void metadata() {
//            assertThat(epubBook.getId()).isEqualTo("my-id");
//            assertThat(epubBook.getLanguage()).isEqualTo("en");
//            assertThat(epubBook.getTitle()).isEqualTo("my-title");
//            assertThat(epubBook.getAuthor()).isEqualTo("my-editor");
//        }
//
//        @Test
//        void coverImage() {
//            Content cover = epubBook.getContents().get(0);
//            assertThat(cover.getMediaType()).isEqualTo("image/jpeg");
//            assertThat(cover.getHref()).isEqualTo("cover.jpg");
//            assertThat(cover.getProperties()).isEqualTo("cover-image");
//            assertThat(cover.getContent()).asHexString().startsWith("FFD8FFE000");
//            assertThat(cover.getContent()).asHexString().endsWith("3F101FFFD9");
//            assertThat(cover.getContent()).hasSize(542);
//            assertThat(cover.isLinear()).isTrue();
//            assertThat(cover.isSpine()).isFalse();
//            assertThat(cover.isToc()).isFalse();
//        }
//
//        @Test
//        void coverPage() {
//            Content content = epubBook.getContents().get(1);
//            assertThat(content.getMediaType()).isEqualTo("application/xhtml+xml");
//            assertThat(content.getHref()).isEqualTo("cover.html");
//            assertThat(content.getProperties()).isNull();
//            assertThat(content.getContent()).asString()
//                    .contains("<title>Cover</title>")
//                    .contains("<body><img src=\"cover.jpg\" style=\"height:100%\"/></body>");
//            assertThat(content.isLinear()).isTrue();
//            assertThat(content.isSpine()).isTrue();
//            assertThat(content.isToc()).isFalse();
//        }
//
//        @Test
//        void titlePage() {
//            Content content = epubBook.getContents().get(2);
//            assertThat(content.getMediaType()).isEqualTo("application/xhtml+xml");
//            assertThat(content.getHref()).isEqualTo("content/prelude-1.html");
//            assertThat(content.getProperties()).isNull();
//            assertThat(content.getContent()).asString()
//                    .startsWith("<html><head><title>test prelude 1 title</title></head>\n<body>")
//                    .contains("<h1>Document Title</h1>")
//                    .contains("<p>document body</p>")
//                    .endsWith("\n</body>\n</html>");
//            assertThat(content.isLinear()).isTrue();
//            assertThat(content.isSpine()).isTrue();
//            assertThat(content.isToc()).isFalse();
//        }
//
//    }
}