package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
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
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public Review add(Review review) {
        if (isInvalidReview(review))
            throw new ValidationDataException("Некорректные данные ревью.");
        if (filmStorage.get(review.getFilmId()) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", review.getFilmId()));
        if (userStorage.get(review.getUserId()) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", review.getUserId()));

        Review result = reviewStorage.add(review);
        long userId = result.getUserId();
        long entityId = result.getReviewId();
        eventStorage.addEvent(userId, entityId, EventType.REVIEW, OperationType.ADD);
        return result;
    }

    public Review update(Review review) {
        if (isInvalidReview(review))
            throw new ValidationDataException("Некорректные данные ревью.");
        if (filmStorage.get(review.getFilmId()) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", review.getFilmId()));
        if (userStorage.get(review.getUserId()) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", review.getUserId()));

        Review result = reviewStorage.update(review, 0);
        long userId = result.getUserId();
        long entityId = result.getReviewId();
        eventStorage.addEvent(userId, entityId, EventType.REVIEW, OperationType.UPDATE);
        return result;
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
        if (listSize < 0)
            throw new ValidationDataException("Некорректные данные запроса.");
        List<Review> result = reviewStorage.getAll();

        if (film_id == null)
            return result.stream()
                    .limit(listSize)
                    .collect(Collectors.toList());
        else {
            if (filmStorage.get(film_id) == null) throw new ValidationNotFoundException(String
                    .format("filmId=%s не найден.", film_id));
            return result.stream()
                    .filter(review -> review.getFilmId().equals(film_id))
                    .limit(listSize)
                    .collect(Collectors.toList());
        }
    }

    public void remove(Long id) {
        long userId = reviewStorage.get(id).getUserId();
        eventStorage.addEvent(userId, id, EventType.REVIEW, OperationType.REMOVE);
        reviewStorage.remove(id);
    }

    public void clear() {
        reviewStorage.clear();
    }

    public Review changeUseful(Long id, Long userId, boolean toAdd, boolean isLike) {
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));

        Review review = reviewStorage.get(id);
        if (review == null) throw new ValidationNotFoundException(String
                .format("reviewId=%s не найден.", id));

        List<Long> userLikes = reviewLikesStorage.getUserLikes(userId);
        List<Long> userDislikes = reviewLikesStorage.getUserDislikes(userId);

        if (toAdd)          // добавить оценку
            if (isLike) {   // лайк
                if (userLikes.contains(id)) throw new ValidationDataException(String
                        .format("userId=%s уже поставил лайк reviewId=%s.", userId, id));

                // изменение оценки с дизлайка на лайк
                if (userDislikes.contains(id)) {
                    reviewLikesStorage.remove(id, userId);
                    review.setUseful(review.getUseful() + 1);
                }

                reviewLikesStorage.add(id, userId, isLike);
                review.setUseful(review.getUseful() + 1);
            } else {        // дизлайк
                if (userDislikes.contains(id)) throw new ValidationDataException(String
                        .format("userId=%s уже поставил дизлайк reviewId=%s.", userId, id));

                // изменение оценки с лайка на дизлайк
                if (userLikes.contains(id)) {
                    reviewLikesStorage.remove(id, userId);
                    review.setUseful(review.getUseful() - 1);
                }

                reviewLikesStorage.add(id, userId, isLike);
                review.setUseful(review.getUseful() - 1);
            }
        else                // удалить оценку
            if (isLike) {   // лайк
                if (userDislikes.contains(id)) throw new ValidationDataException(String
                        .format("userId=%s поставил дизлайк reviewId=%s, а не лайк.", userId, id));
                if (!userLikes.contains(id)) throw new ValidationDataException(String
                        .format("userId=%s не поставил лайк reviewId=%s.", userId, id));

                reviewLikesStorage.remove(id, userId);
                review.setUseful(review.getUseful() - 1);
            } else {        // дизлайк
                if (userLikes.contains(id)) throw new ValidationDataException(String
                        .format("userId=%s поставил лайк reviewId=%s, а не дизлайк.", userId, id));
                if (!userDislikes.contains(id)) throw new ValidationDataException(String
                        .format("userId=%s не поставил дизлайк reviewId=%s.", userId, id));

                reviewLikesStorage.remove(id, userId);
                review.setUseful(review.getUseful() + 1);
            }
        return reviewStorage.update(review, 1);
    }

    public void updateUsefulForRemoveUser(Long userId) {
        reviewLikesStorage.getUserLikes(userId).stream()
                .map(reviewStorage::get)
                .forEach(review -> review.setUseful(review.getUseful() - 1));
        reviewLikesStorage.getUserDislikes(userId).stream()
                .map(reviewStorage::get)
                .forEach(review -> review.setUseful(review.getUseful() + 1));
        // Удаление самих записей из базы выполнится каскадно при удалении пользователя.
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
