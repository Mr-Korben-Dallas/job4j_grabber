package ru.job4j.grabber;

import ru.job4j.grabber.entity.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ExecuteHabrCareerParse {
    private static final String LINK = "https://career.habr.com";

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("psqlstore.properties")
        ) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PsqlStore store = new PsqlStore(properties)) {
            DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
            HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
            for (Post post : new ArrayList<>(habrCareerParse.list(LINK))) {
                store.save(post);
            }
            List<Post> posts = store.getAll();
            posts.forEach(System.out::println);
            Post post = store.findById(12);
            System.out.println(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
