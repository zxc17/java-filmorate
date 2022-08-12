package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long filmId, long genreId) {
        String sql = "insert into FILM_GENRE(FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";
        if (jdbcTemplate.update(sql, filmId, genreId) == 0) throw new StorageException(
                String.format("Ошибка при добавлении в БД FILM_GENRE, filmID=%s, genreID=%s.", filmId, genreId));
    }

    @Override
    public List<Long> get(long filmId) {
        String sql = "select * from FILM_GENRE where FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToLike, filmId);
    }

    @Override
    public void update(long filmId, Set<Genre> genres) {
        if (genres == null)
            throw new StorageException("Ошибка при внутреннем запросе обновления жанров. Отсутствует список.");
        Properties properties = new Properties();
        String url, login, password;
        try {
            properties.load(new FileReader(
                    Paths.get("src", "main", "resources", "application.properties").toFile()));
            url = properties.getProperty("spring.datasource.url");
            login = properties.getProperty("spring.datasource.username");
            password = properties.getProperty("spring.datasource.password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(url, login, password);
             Statement statement = connection.createStatement())
        {
            connection.setAutoCommit(false);
            statement.addBatch(String.format("delete from FILM_GENRE where FILM_ID = %s", filmId));
            if (genres.size() > 0) {
                StringBuilder sql = new StringBuilder("insert into FILM_GENRE (FILM_ID, GENRE_ID) values ");
                Iterator<Genre> i = genres.iterator();
                do {
                    long genreId = i.next().getId();
                    sql.append(String.format("(%s, %s)", filmId, genreId));
                    if (i.hasNext())
                        sql.append(", ");
                    else {
                        sql.append(";");
                        break;
                    }
                } while (true);
                statement.addBatch(sql.toString());
            }
            statement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(long filmId, long genreId) {
        String sql = "delete from FILM_GENRE where FILM_ID = ? and GENRE_ID = ?";
        if (jdbcTemplate.update(sql, filmId, genreId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из БД FILM_GENRE, filmID=%s, genreID=%s.", filmId, genreId));
    }

    private Long mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("GENRE_ID");
    }
}
