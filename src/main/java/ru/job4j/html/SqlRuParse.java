package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SqlRuParse {

    private static int hrefIndex = 0;
    private static int dateIndex = 5;
    private static String attrKey = "href";

    public static void main(String[] args) throws IOException {
        int page = 0;
        while (page < 6) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + page).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Post post = new Post(td);
                System.out.println(post.getLink());
                System.out.println(post.getTitle());
                System.out.println(post.getDate());
                System.out.println(detail(post));
            }
            page++;
        }
    }

    public static String detail(Post post) throws IOException {
        String description = "";
        Document doc = Jsoup.connect(post.getLink()).get();
        Element row = doc.select(".msgBody").get(1);
        for (var item : row.textNodes()) {
            description = description + item + System.lineSeparator();
        }
        return description;
    }
}