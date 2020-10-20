package net.kemitix.binder.spi;

import lombok.Getter;
import lombok.Setter;

import javax.enterprise.inject.Vetoed;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Vetoed
public class Metadata {

    private String id;
    private int issue;
    private String date;
    private String language;
    private String title;
    private String subtitle;
    private String kdpSubtitle;
    private String description;
    private String isbn;
    private String editor;
    private String cover;
    private String coverArtist;
    private List<String> contents = new ArrayList<>();

}
