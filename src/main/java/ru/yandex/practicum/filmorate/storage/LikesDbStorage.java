package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Rate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DbStorageUtil dbStorageUtil;

    @Override
    public void put(long filmId, long userId, int rate) {
        String sql = "merge into LIKES(FILM_ID, USER_ID, RATE) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, rate);
    }

    @Override
    public Set<Rate> get(long filmId) {
        String sql = "select * from LIKES where FILM_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToLike, filmId));
    }

    @Override
    public List<Long> getIdFilmsRatedByUser(long userId) {
        String sql = "" +
                "select FILM_ID " +
                "from LIKES " +
                "where USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilmId, userId);
    }

    @Override
    public void update(long filmId, Set<Rate> likes) {
        dbStorageUtil.updateTableLikes(
                "LIKES",
                "FILM_ID", filmId,
                "USER_ID", likes);
    }

    @Override
    public void remove(long filmId, long userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из БД LIKES, filmID=%s, userID=%s.", filmId, userId));
    }

    private Rate mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return Rate.builder()
                .userId(resultSet.getLong("USER_ID"))
                .rate(resultSet.getDouble("RATE"))
                .build();
    }

    private Long mapRowToFilmId(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("FILM_ID");
    }
}
