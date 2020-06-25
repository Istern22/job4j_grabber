package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
                Element href = td.child(hrefIndex);
                System.out.println(href.attr(attrKey));
                System.out.println(href.text());
                Element date = td.parent().child(dateIndex);
                System.out.println(date.text() + System.lineSeparator());
            }
            page++;
        }
    }
}