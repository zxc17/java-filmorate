package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface ReviewLikesStorage {

    void add(long reviewId, long userId, boolean isPositive);

    List<Long> getReviewLikes(long reviewId);

    List<Long> getReviewDislikes(long reviewId);

    List<Long> getUserLikes(long userId);

    List<Long> getUserDislikes(long userId);

    void remove(long reviewId, long userId);
}
