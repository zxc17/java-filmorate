package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DbStorageUtil dbStorageUtil;

    @Override
    public List<Long> getDirectorByFilm(long filmId) {
        String sql = "select * from FILM_DIRECTOR where FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToDirector, filmId);
    }

    @Override
    public void updateDirectorByFilm(long filmId, Set<Director> directors) {
        dbStorageUtil.updateTable("FILM_DIRECTOR", "FILM_ID", filmId,
                "DIRECTOR_ID", directors.stream().map(Director::getId).collect(Collectors.toSet()));
    }

    @Override
    public void clearDirectorByFilm(long filmId) {
        String sql = "delete from FILM_DIRECTOR where FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Long mapRowToDirector(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("DIRECTOR_ID");
    }

}
