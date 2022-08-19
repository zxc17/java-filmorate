package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface ReviewLikesStorage {

    void add(long reviewId, long userId);

    List<Long> getReviewLikes(long reviewId);

    List<Long> getUserLikes(long userId);

    void remove(long reviewId, long userId);
}
