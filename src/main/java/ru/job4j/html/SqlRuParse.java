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

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            Element date = td.parent().child(5);
            System.out.println(date.text() + "\n");
        }
    }

    public static String toDate(String dateString) {
        LocalDateTime formatDate;
        if (dateString.contains("сегодня")) {
            formatDate = LocalDateTime.now();
        } else if (dateString.contains("вчера")) {
            LocalDateTime now = LocalDateTime.now();
            formatDate = now.minusDays(1);
        } else {
            String[] items = dateString.split(" ");
            switch (items[1]) {
                case "янв" : items[1] = "01";
                    break;
                case "фев" : items[1] = "02";
                    break;
                case "мар" : items[1] = "03";
                    break;
                case "апр" : items[1] = "04";
                    break;
                case "май" : items[1] = "05";
                    break;
                case "июн" : items[1] = "06";
                    break;
                case "июл" : items[1] = "07";
                    break;
                case "авг" : items[1] = "08";
                    break;
                case "сен" : items[1] = "09";
                    break;
                case "окт" : items[1] = "10";
                    break;
                case "ноя" : items[1] = "11";
                    break;
                case "дек" : items[1] = "12";
                    break;
                default : break;
            }
            dateString = String.join(" ", items);
            formatDate = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("d MM yy, HH:mm"));
        }
        return formatDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", new Locale("ru")));
    }
}