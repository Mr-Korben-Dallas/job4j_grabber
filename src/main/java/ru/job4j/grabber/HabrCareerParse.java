package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final String NEXT_PAGE_LINK = String.format("%s?page=", PAGE_LINK);
    private static final int NUMBER_OF_PAGES_TO_PARSE = 5;

    public static void main(String[] args) {
        for (int i = 1; i <= NUMBER_OF_PAGES_TO_PARSE; i++) {
            parse(i);
        }
    }

    private static void parse(int pageNumber) {
        try {
            Connection connection = Jsoup.connect(NEXT_PAGE_LINK + pageNumber);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateTimeElement = row.select(".vacancy-card__date").first();
                Element timeElement = dateTimeElement.child(0);
                String dateTimeValue = timeElement.attr("datetime");
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n", dateTimeValue, vacancyName, link);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
