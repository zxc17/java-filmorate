package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public void remove(long filmId, long userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из БД LIKES, filmID=%s, userID=%s.", filmId, userId));
    }

    private Long mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("USER_ID");
    }
}
