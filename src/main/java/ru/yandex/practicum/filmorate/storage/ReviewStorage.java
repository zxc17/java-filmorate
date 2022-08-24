package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review review, int mode);

    Map<Review, Boolean> getReviewListByUser(Long userId);

    void remove(Long id);

    Review get(Long id);

    List<Review> getAll();

    void clear();
}
