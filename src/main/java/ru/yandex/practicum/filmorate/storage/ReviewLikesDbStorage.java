package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewLikesDbStorage implements ReviewLikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void put(long reviewId, long userId, boolean isPositive) {
        String sql = "merge into REVIEW_LIKES (REVIEW_ID, USER_ID, IS_POSITIVE) " +
                "values (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isPositive);
    }

    @Override
    public List<Long> getReviewLikes(long reviewId) {
        String sql = "select USER_ID from REVIEW_LIKES where REVIEW_ID = ? and IS_POSITIVE = true";
        return jdbcTemplate.query(sql, this::rowToUserId, reviewId);
    }

    @Override
    public List<Long> getReviewDislikes(long reviewId) {
        String sql = "select USER_ID from REVIEW_LIKES where REVIEW_ID = ? and IS_POSITIVE = false";
        return jdbcTemplate.query(sql, this::rowToUserId, reviewId);
    }

    @Override
    public List<Long> getUserLikes(long userId) {
        String sql = "select REVIEW_ID from REVIEW_LIKES where USER_ID = ? and IS_POSITIVE = true";
        return jdbcTemplate.query(sql, this::rowToReviewId, userId);
    }

    @Override
    public List<Long> getUserDislikes(long userId) {
        String sql = "select REVIEW_ID from REVIEW_LIKES where USER_ID = ? and IS_POSITIVE = false";
        return jdbcTemplate.query(sql, this::rowToReviewId, userId);
    }

    @Override
    public Boolean getReviewLikeByUser(Long reviewId, Long userId) {
        String sql = "" +
                "select IS_POSITIVE " +
                "from REVIEW_LIKES " +
                "where REVIEW_ID = ? and USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Boolean.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public void remove(long reviewId, long userId) {
        String sql = "delete from REVIEW_LIKES where REVIEW_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, reviewId, userId) == 0) throw new StorageException(
                String.format("Ошибка при удалении из таблицы REVIEW_LIKES, reviewID=%s, userID=%s.", reviewId, userId));
    }

    private Long rowToUserId(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("USER_ID");
    }

    private Long rowToReviewId(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getLong("REVIEW_ID");
    }
}
