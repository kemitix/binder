package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
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
