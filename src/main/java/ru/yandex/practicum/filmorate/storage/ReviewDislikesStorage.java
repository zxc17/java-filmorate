package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface ReviewDislikesStorage {

    void add(long reviewId, long userId);

    List<Long> getReviewDislikes(long reviewId);

    List<Long> getUserDislikes(long userId);

    void remove(long reviewId, long userId);
}
