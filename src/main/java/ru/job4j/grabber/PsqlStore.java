package ru.job4j.grabber;

import ru.job4j.grabber.entity.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private static final String PROPERTY_DB_URL = "url";
    private static final String PROPERTY_DB_USERNAME = "username";
    private static final String PROPERTY_DB_PASSWORD = "password";
    private static final String PROPERTY_DB_JDBC_DRIVER = "jdbc.driver";
    private static final String TABLE_POSTS_QUERY_CREATE = "insert into posts (title, description, link, posted_at) values (?, ?, ?, ?) ON CONFLICT (link) DO NOTHING;";
    private static final String TABLE_POSTS_QUERY_SELECT_ALL = "select * from posts;";
    private static final String TABLE_POSTS_QUERY_SELECT_BY_ID = "select * from posts where id = ?;";
    private static final String TABLE_POSTS_FIELD_ID = "id";
    private static final String TABLE_POSTS_FIELD_TITLE = "title";
    private static final String TABLE_POSTS_FIELD_DESCRIPTION = "description";
    private static final String TABLE_POSTS_FIELD_LINK = "link";
    private static final String TABLE_POSTS_FIELD_POSTED_AT = "posted_at";
    private Connection connection;

    public PsqlStore(Properties properties) {
        try {
            Class.forName(properties.getProperty(PROPERTY_DB_JDBC_DRIVER));
            connection = DriverManager.getConnection(
                    properties.getProperty(PROPERTY_DB_URL),
                    properties.getProperty(PROPERTY_DB_USERNAME),
                    properties.getProperty(PROPERTY_DB_PASSWORD)
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(TABLE_POSTS_QUERY_CREATE)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getDescription());
            preparedStatement.setString(3, post.getLink());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(post.getPostedAt()));
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> listOfPosts = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(TABLE_POSTS_QUERY_SELECT_ALL)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    listOfPosts.add(getPostFromResultSet(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfPosts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(TABLE_POSTS_QUERY_SELECT_BY_ID)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    post = getPostFromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    private Post getPostFromResultSet(ResultSet resultSet) throws Exception {
        return new Post(
                resultSet.getInt(TABLE_POSTS_FIELD_ID),
                resultSet.getString(TABLE_POSTS_FIELD_TITLE),
                resultSet.getString(TABLE_POSTS_FIELD_DESCRIPTION),
                resultSet.getString(TABLE_POSTS_FIELD_LINK),
                resultSet.getTimestamp(TABLE_POSTS_FIELD_POSTED_AT).toLocalDateTime()
        );
    }
}