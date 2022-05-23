package ru.job4j.grabber;

import ru.job4j.grabber.entity.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HarbCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class ExecuteHabrCareerParse {
    private static final String LINK = "https://career.habr.com";

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader()
                .getResourceAsStream("psqlstore.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore store = new PsqlStore(properties);
        DateTimeParser dateTimeParser = new HarbCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
        List<Post> listOfPosts = habrCareerParse.list(LINK);
        listOfPosts.forEach(store::save);
        List<Post> posts = store.getAll();
        posts.forEach(System.out::println);
        Post post = store.findById(12);
        System.out.println(post);
    }
}