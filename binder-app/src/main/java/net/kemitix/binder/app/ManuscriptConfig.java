package net.kemitix.binder.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@Setter
@Getter
@ToString
@ApplicationScoped
public class ManuscriptConfig {

    private String id;
    private int issue;
    private String date;
    private String title;
    private String subtitle;
    private String kdpSubtitle;
    private String description;
    private String isbn;
    private String editor;
    private String cover;
    private String coverArtist;

    private List<String> prelude;
    private List<String> contents;
    private List<String> coda;
}
