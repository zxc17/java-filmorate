package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        dbStorageUtil.updateTable("FILM_GENRE", "FILM_ID", filmId,
                "GENRE_ID", genres.stream().map(Genre::getId).collect(Collectors.toSet()));
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
