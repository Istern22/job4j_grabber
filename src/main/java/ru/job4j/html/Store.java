package ru.job4j.html;

import java.util.List;

public interface Store {
    void save(Post post);
    void saveAll(List<Post> posts);
    List<Post> getAll();
}
