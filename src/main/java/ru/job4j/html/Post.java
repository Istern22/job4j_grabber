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

    public Post(String link) {
        this.link = link;
    }

    public Post(String link, String title, String date, String description) {
        this.link = link;
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public String getLink() {
        return this.link;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return this.date;
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

    @Override
    public String toString() {
        return link + title + date + description;
    }
}
