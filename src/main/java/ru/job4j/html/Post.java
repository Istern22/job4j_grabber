package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Post {

    private String link;
    private String title;
    private String description;
    private String date;

    private Element td;

    public Post(Element td) {
        this.td = td;
    }

    public String getLink() {
        return td.child(0).attr("href");
    }

    public String getTitle() {
        return td.child(0).text();
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return td.parent().child(5).text();
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
