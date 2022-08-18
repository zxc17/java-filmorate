package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Review add(Review review) {
        if (isInvalidReview(review))
            throw new ValidationDataException("Некорректные данные ревью.");
        if (filmStorage.get(review.getFilmId()) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", review.getFilmId()));
        if (userStorage.get(review.getUserId()) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", review.getUserId()));

        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        if (isInvalidReview(review))
            throw new ValidationDataException("Некорректные данные ревью.");
        if (filmStorage.get(review.getFilmId()) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", review.getFilmId()));
        if (userStorage.get(review.getUserId()) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", review.getUserId()));

        return reviewStorage.update(review, 0);
    }

    public Review get(Long id) {
        Review review = reviewStorage.get(id);

        if (review == null)
            throw new ValidationNotFoundException(String.format("reviewId=%s не найден.", id));

        return review;
    }

    public List<Review> getAll() {
        return reviewStorage.getAll();
    }

    public List<Review> getList(Long film_id, Integer listSize) {
        List<Review> result = reviewStorage.getAll();

        if (listSize == -1)
            listSize = 10;

        if (film_id == -1)
            return result.stream()
                    .limit(listSize)
                    .collect(Collectors.toList());
        else
            return result.stream()
                    .filter(review -> review.getFilmId().equals(film_id))
                    .limit(listSize)
                    .collect(Collectors.toList());
    }

    public void remove(Long id) {
        reviewStorage.remove(id);
    }

    public void clear() {
        reviewStorage.clear();
    }

    public Review addUseful(Long id, Long userId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));

        Review review = reviewStorage.get(id);
        review.setUseful(review.getUseful() + 1);
        return reviewStorage.update(review, 1);
    }

    public Review lowerUseful(Long id, Long userId) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));

        Review review = reviewStorage.get(id);
        review.setUseful(review.getUseful() - 1);
        return reviewStorage.update(review, 1);
    }

    private boolean isInvalidReview(Review review) {
        if (review.getIsPositive() == null ||
            review.getFilmId() == null ||
            review.getUserId() == null ||
            review.getContent() == null ||
            review.getContent().isBlank() ||
            review.getContent().length() > 200)
            return true;
        else
            return false;
    }
}
