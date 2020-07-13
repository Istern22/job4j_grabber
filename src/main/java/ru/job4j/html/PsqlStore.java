package ru.job4j.html;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cn;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("jdbc.driver"));
            cn = DriverManager.getConnection(
                    config.getProperty("jdbc.url"),
                    config.getProperty("jdbc.username"),
                    config.getProperty("jdbc.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.load(PsqlStore.class.getClassLoader().getResourceAsStream("store.properties"));
        PsqlStore store = new PsqlStore(config);
        SqlRuParse parse = new SqlRuParse();
        List<Post> posts = parse.list("https://www.sql.ru/forum/job-offers/1");
        for (Post post : posts) {
            store.save(parse.detail(post.getLink()));
        }
        store.saveAll(posts);
        List<Post> result = store.getAll();
        for (Post item : result) {
            System.out.println(item.toString());
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement prep = cn.prepareStatement(
                "INSERT INTO post (link, name, created, text) VALUES (?, ?, ?, ?)")) {
            prep.setString(1, post.getLink());
            prep.setString(2, post.getTitle());
            prep.setDate(3, java.sql.Date.valueOf(DateUtils.toDate(post.getDate()).toLocalDate()));
            prep.setString(4, post.getDescription());
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveAll(List<Post> posts) {
        SqlRuParse parse = new SqlRuParse();
        try {
            cn.setAutoCommit(false);
            PreparedStatement prep = cn.prepareStatement(
                    "INSERT INTO post (link, name, created, text) VALUES (?, ?, ?, ?)");
            for (Post postLink : posts) {
                Post post = parse.detail(postLink.getLink());
                prep.setString(1, post.getLink());
                prep.setString(2, post.getTitle());
                prep.setDate(3, java.sql.Date.valueOf(DateUtils.toDate(post.getDate()).toLocalDate()));
                prep.setString(4, post.getDescription());
                prep.addBatch();
            }
            prep.executeBatch();
            cn.commit();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        try (ResultSet resultSet = cn.createStatement().executeQuery("SELECT * FROM post;")) {
            while (resultSet.next()) {
                String link = resultSet.getString("link");
                String name = resultSet.getString("name");
                String date = resultSet.getString("created");
                String text = resultSet.getString("text");
                result.add(new Post(link, name, date, text));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
