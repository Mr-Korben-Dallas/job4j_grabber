package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.entity.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final String LINK_NEXT_SUFFIX = "/vacancies/java_developer?page=";
    private static final String VACANCY_CARD_ELEMENT_MAIN = ".vacancy-card__inner";
    private static final String VACANCY_CARD_ELEMENT_TITLE = ".vacancy-card__title";
    private static final String VACANCY_CARD_ELEMENT_TITLE_ATTR_HREF = "href";
    private static final String VACANCY_CARD_ELEMENT_DATE = ".vacancy-card__date";
    private static final String VACANCY_CARD_ELEMENT_DATE_ATTR_DATETIME = "datetime";
    private static final String VACANCY_CARD_DESCRIPTION = ".job_show_description__vacancy_description";
    private static final int NUMBER_OF_PAGES_TO_PARSE = 5;
    private final DateTimeParser dateTimeParser;
    private List<Post> listOfPosts = new ArrayList<>();
    private String sourceLink;
    private String pageNextLink;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        preparePageLinks(link);
        for (int pageNumber = 1; pageNumber <= NUMBER_OF_PAGES_TO_PARSE; pageNumber++) {
            parse(link, pageNumber);
        }
        return listOfPosts;
    }

    private void preparePageLinks(String link) {
        this.sourceLink = link;
        this.pageNextLink = link + LINK_NEXT_SUFFIX;
    }

    private void parse(String url, int pageNumber) {
        Document document = getPageDocument(this.pageNextLink + pageNumber);
        Elements rows = document.select(VACANCY_CARD_ELEMENT_MAIN);
        rows.forEach(row -> {
            extractVacancyDetailsToPostList(row);
        });
    }

    private void extractVacancyDetailsToPostList(Element row) {
        Element titleElement = row.select(VACANCY_CARD_ELEMENT_TITLE).first();
        Element linkElement = titleElement.child(0);
        Element dateTimeElement = row.select(VACANCY_CARD_ELEMENT_DATE).first();
        Element timeElement = dateTimeElement.child(0);
        String dateTimeValue = timeElement.attr(VACANCY_CARD_ELEMENT_DATE_ATTR_DATETIME);
        String vacancyTitle = titleElement.text();
        String vacancyLink = this.sourceLink + linkElement.attr(VACANCY_CARD_ELEMENT_TITLE_ATTR_HREF);
        String vacancyDescription = retrieveDescription(vacancyLink);
        LocalDateTime vacancyPostedAt = dateTimeParser.parse(dateTimeValue);
        Post post = new Post(
                vacancyTitle,
                vacancyLink,
                vacancyDescription,
                vacancyPostedAt
        );
        listOfPosts.add(post);
    }

    private Document getPageDocument(String url) {
        Document document = null;
        try {
            Connection connection = Jsoup.connect(url);
            document = connection.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    private String retrieveDescription(String link) {
        Document document = getPageDocument(link);
        return document.select(VACANCY_CARD_DESCRIPTION).text();
    }
}