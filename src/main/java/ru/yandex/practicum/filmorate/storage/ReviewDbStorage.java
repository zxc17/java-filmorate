package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();

        values.put("CONTENT", review.getContent());
        values.put("IS_POSITIVE", review.getIsPositive());
        values.put("USER_ID", review.getUserId());
        values.put("FILM_ID", review.getFilmId());
        values.put("USEFULNESS", 0);

        KeyHolder keyHolder = jdbcInsert
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("REVIEW_ID")
                .executeAndReturnKeyHolder(values);
        review.setReviewId(keyHolder.getKey().longValue());

        return review;
    }

    @Override
    public Review update(Review review, int mode) {
        String sql;

        switch (mode) { // вид обновления ревью: 0 -- обычное, 1 -- удаление/добавление лайков/дизлайков
            case 0:
                sql = "update REVIEWS " +
                        "set CONTENT = ?, IS_POSITIVE = ? " +
                        "where REVIEW_ID = ?";

                if (jdbcTemplate.update(sql,
                        review.getContent(), review.getIsPositive(),
                        review.getReviewId()) == 0)
                    throw new StorageException(String.format("Ошибка при обновлении данных в таблице REVIEWS, id=%s.",
                            review.getReviewId()));
                break;
            case 1:
                sql = "update REVIEWS " +
                        "set USEFULNESS = ? " +
                        "where REVIEW_ID = ?";

                if (jdbcTemplate.update(sql,
                        review.getUseful(),
                        review.getReviewId()) == 0)
                    throw new StorageException(String.format("Ошибка при обновлении данных в таблице REVIEWS, id=%s.",
                            review.getReviewId()));
                break;
            default:
                throw new StorageException(String.format("Ошибка при обновлении данных в таблице REVIEWS, id=%s.",
                        review.getReviewId()));
        }
        return review;
    }

    @Override
    public Review get(Long id) {
        String sql = "select * " +
                "from REVIEWS as R " +
                "where R.REVIEW_ID = ?";
        List<Review> query = jdbcTemplate.query(sql, this::rowToReview, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из таблицы REVIEWS, id=%s.", id));
        }
    }

    @Override
    public List<Review> getAll() {
        String sql = "select * " +
                "from REVIEWS as R " +
                "order by R.USEFULNESS desc";
        return jdbcTemplate.query(sql, this::rowToReview);
    }

    @Override
    public void remove(Long id) {
        String sql = "delete from REVIEWS where REVIEW_ID = ?";
        if (jdbcTemplate.update(sql, id) == 0)
            throw new StorageException(String.format("Ошибка при удалении из таблицы REVIEWS, id=%s.", id));
    }

    @Override
    public void clear() {
        jdbcTemplate.update("delete from REVIEWS");
    }

    private Review rowToReview(ResultSet resultSet, int numRow) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("REVIEW_ID"))
                .content(resultSet.getString("CONTENT"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(resultSet.getInt("USEFULNESS"))
                .build();
    }
}
