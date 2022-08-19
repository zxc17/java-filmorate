package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDislikesStorage;
import ru.yandex.practicum.filmorate.storage.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final ReviewDislikesStorage reviewDislikesStorage;
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

        if (listSize < 0)
            throw new ValidationDataException("Некорректные данные запроса.");

        if (film_id == null)
            return result.stream()
                    .limit(listSize)
                    .collect(Collectors.toList());
        else {
            if (filmStorage.get(film_id) == null)
                throw new ValidationNotFoundException(String.format("filmId=%s не найден.", film_id));

            return result.stream()
                    .filter(review -> review.getFilmId().equals(film_id))
                    .limit(listSize)
                    .collect(Collectors.toList());
        }
    }

    public void remove(Long id) {
        reviewStorage.remove(id);
    }

    public void clear() {
        reviewStorage.clear();
    }

    public Review changeUseful(Long id, Long userId, boolean toAdd, boolean isLike) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));

        Review review = reviewStorage.get(id);

        if (review == null)
            throw new ValidationNotFoundException(String.format("reviewId=%s не найден.", id));

        List<Long> userLikes = reviewLikesStorage.getUserLikes(userId);
        List<Long> userDislikes = reviewDislikesStorage.getUserDislikes(userId);



        if (toAdd)          // добавить оценку
            if (isLike) {   // лайк
                if (userLikes.contains(id))
                    throw new ValidationDataException(String
                            .format("userId=%s уже поставил лайк reviewId=%s.", userId, id));

                if (userDislikes.contains(id)) {
                    reviewDislikesStorage.remove(id, userId);
                    review.setUseful(review.getUseful() + 1);
                }

                reviewLikesStorage.add(id, userId);
                review.setUseful(review.getUseful() + 1);
            } else {        // дизлайк
                if (userDislikes.contains(id))
                    throw new ValidationDataException(String
                            .format("userId=%s уже поставил дизлайк reviewId=%s.", userId, id));

                if (userLikes.contains(id)) {
                    reviewLikesStorage.remove(id, userId);
                    review.setUseful(review.getUseful() - 1);
                }

                reviewDislikesStorage.add(id, userId);
                review.setUseful(review.getUseful() - 1);
            }
        else                // удалить оценку
            if (isLike) {   // лайк
                if (userDislikes.contains(id))
                    throw new ValidationDataException(String
                            .format("userId=%s поставил дизлайк reviewId=%s, а не лайк.", userId, id));
                if (!userLikes.contains(id))
                    throw new ValidationDataException(String
                            .format("userId=%s не поставил лайк reviewId=%s.", userId, id));

                reviewLikesStorage.remove(id, userId);
                review.setUseful(review.getUseful() - 1);
            } else {        // дизлайк
                if (userLikes.contains(id))
                    throw new ValidationDataException(String
                            .format("userId=%s поставил лайк reviewId=%s, а не дизлайк.", userId, id));
                if (!userDislikes.contains(id))
                    throw new ValidationDataException(String
                            .format("userId=%s не поставил дизлайк reviewId=%s.", userId, id));

                reviewDislikesStorage.remove(id, userId);
                review.setUseful(review.getUseful() + 1);
            }

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
