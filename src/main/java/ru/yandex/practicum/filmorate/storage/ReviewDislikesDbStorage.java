package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class ReviewDislikesDbStorage implements ReviewDislikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long reviewId, long userId) {
        String sql = "insert into REVIEW_DISLIKES (REVIEW_ID, USER_ID) " +
                "values (?, ?)";
        if (jdbcTemplate.update(sql, reviewId, userId) == 0) throw new StorageException(
                String.format("Ошибка при добавлении в таблицу REVIEW_DISLIKES, reviewID=%s, userID=%s.", reviewId, userId));
    }

    @Override
    public List<Long> getReviewDislikes(long reviewId) {
        String sql = "select USER_ID from REVIEW_DISLIKES where REVIEW_ID = ?";
        return jdbcTemplate.query(sql, this::rowToUserId, reviewId);
    }

    @Override
    public List<Long> getUserDislikes(long userId) {
        String sql = "select REVIEW_ID from REVIEW_DISLIKES where USER_ID = ?";
        return jdbcTemplate.query(sql, this::rowToReviewId, userId);
    }

    @Override
    public void remove(long reviewId, long userId) {
        String sql = "delete from REVIEW_DISLIKES where REVIEW_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, reviewId, userId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из таблицы REVIEW_DISLIKES, reviewID=%s, userID=%s.", reviewId, userId));
    }

    private Long rowToUserId(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("USER_ID");
    }

    private Long rowToReviewId(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("REVIEW_ID");
    }
}
