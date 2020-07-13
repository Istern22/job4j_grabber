package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    public static void main(String[] args) throws IOException {
        SqlRuParse parse = new SqlRuParse();
        int page = 0;
        while (page < 6) {
            List<Post> posts = parse.list("https://www.sql.ru/forum/job-offers/" + page);
            for (Post post : posts) {
                System.out.println(parse.detail(post.getLink()));
            }
            page++;
        }
    }

    @Override
    public List<Post> list(String link) {
        List<Post> result = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                String postLink = td.child(0).attr("href");
                result.add(new Post(postLink));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        String title = doc.select(".messageHeader").get(0).ownText();
        String date = doc.select(".msgFooter").get(0).ownText().substring(0, 16);
        String description = "";
        Element row = doc.select(".msgBody").get(1);
        for (var item : row.textNodes()) {
            description = description + item + System.lineSeparator();
        }
        return new Post(link, title, date, description);
    }
}