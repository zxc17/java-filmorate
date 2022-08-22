package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Primary
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long filmId, long userId) {
        String sql = "merge into LIKES(FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new StorageException(
                String.format("Ошибка при добавлении в БД LIKES, filmID=%s, userID=%s.", filmId, userId));
    }

    @Override
    public Set<Long> get(long filmId) {
        String sql = "select * from LIKES where FILM_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToLike, filmId));
    }

    @Override
    public HashMap<Film, Double> getLikeListByUser(long userId) {
        String sql = "" +
                "select FILMS.*, MPA_NAME, USER_ID " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "join " +
                "( " +
                "select * from LIKES where USER_ID = ? " +
                ") L " +
                "on FILMS.FILM_ID = L.FILM_ID ";
        SqlRowSet rs = (jdbcTemplate.queryForRowSet(sql, userId));
        HashMap<Film, Double> result = new HashMap<>();
        while (rs.next()) {
            Film film = Film.builder()
                    .id(rs.getLong("FILM_ID"))
                    .name(rs.getString("FILM_NAME"))
                    .description(rs.getString("DESCRIPTION"))
                    .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                    .duration(rs.getLong("DURATION"))
                    .mpa(Mpa.builder()
                            .id(rs.getLong("MPA_ID"))
                            .name(rs.getString("MPA_NAME"))
                            .build())
                    .build();
            double rate = rs.getDouble("USER_ID"); // Оценка фильма. На текущем этапе - 1 или отсутствует.
            result.put(film, rate);
        }
        return result;
    }

    @Override
    public void remove(long filmId, long userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из БД LIKES, filmID=%s, userID=%s.", filmId, userId));
    }

    private Long mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("USER_ID");
    }

}
