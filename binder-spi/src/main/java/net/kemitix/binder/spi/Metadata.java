package net.kemitix.binder.spi;

import lombok.Getter;
import lombok.Setter;

import javax.enterprise.inject.Vetoed;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Vetoed
public class Metadata {

    private String id;
    private int issue;
    private String date;
    private String copyright;
    private String language;
    private String title;
    private String subtitle;
    private String kdpSubtitle;
    private String description;
    private String isbn;
    private String editor;
    private String cover;
    private String coverArtist;
    private File titleFont;
    private boolean kerning;
    private boolean ligatures;
    private int paperbackFontSize;
    private int paperbackFootnoteFontSize;
    private float paperbackPageWidthInches;
    private float paperbackPageHeightInches;
    private float paperbackMarginSides;
    private float paperbackMarginTopBottom;
    private List<String> contents = new ArrayList<>();

}
