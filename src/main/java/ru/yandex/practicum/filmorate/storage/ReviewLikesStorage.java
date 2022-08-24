package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface ReviewLikesStorage {

    void put(long reviewId, long userId, boolean isPositive);

    List<Long> getReviewLikes(long reviewId);

    List<Long> getReviewDislikes(long reviewId);

    List<Long> getUserLikes(long userId);

    List<Long> getUserDislikes(long userId);

    /**
     * Возвращает оценку отзыва пользователем.
     *
     * @param reviewId ID отзыва.
     * @param userId   ID юзера
     * @return true - лайк, false - дизлайк, null - оценки нет.
     */
    Boolean getReviewLikeByUser(Long reviewId, Long userId);

    void remove(long reviewId, long userId);
}
