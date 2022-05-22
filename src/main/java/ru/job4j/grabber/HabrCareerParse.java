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
    private static final String VACANCY_CARD_ELEMENT_MAIN = ".vacancy-card__inner";
    private static final String VACANCY_CARD_ELEMENT_TITLE = ".vacancy-card__title";
    private static final String VACANCY_CARD_ELEMENT_TITLE_ATTR_HREF = "href";
    private static final String VACANCY_CARD_ELEMENT_DATE = ".vacancy-card__date";
    private static final String VACANCY_CARD_ELEMENT_DATE_ATTR_DATETIME = "datetime";
    private static final String VACANCY_CARD_DESCRIPTION = ".job_show_description__vacancy_description";
    private static final int NUMBER_OF_PAGES_TO_PARSE = 5;

    public static void main(String[] args) {
        for (int i = 1; i <= NUMBER_OF_PAGES_TO_PARSE; i++) {
            parse(i);
        }
    }

    private static void parse(int pageNumber) {
        Document document = getPageDocument(NEXT_PAGE_LINK + pageNumber);
        Elements rows = document.select(VACANCY_CARD_ELEMENT_MAIN);
        rows.forEach(row -> {
            Element titleElement = row.select(VACANCY_CARD_ELEMENT_TITLE).first();
            Element linkElement = titleElement.child(0);
            Element dateTimeElement = row.select(VACANCY_CARD_ELEMENT_DATE).first();
            Element timeElement = dateTimeElement.child(0);
            String dateTimeValue = timeElement.attr(VACANCY_CARD_ELEMENT_DATE_ATTR_DATETIME);
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr(VACANCY_CARD_ELEMENT_TITLE_ATTR_HREF));
            System.out.printf("%s %s %s%n", dateTimeValue, vacancyName, link);
        });
    }

    private static Document getPageDocument(String url) {
        Document document = null;
        try {
            Connection connection = Jsoup.connect(url);
            document = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    private static String retrieveDescription(String url) {
        Document document = getPageDocument(url);
        return document.select(VACANCY_CARD_DESCRIPTION).text();
    }
}