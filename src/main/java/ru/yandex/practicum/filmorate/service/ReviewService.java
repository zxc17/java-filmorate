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

    /**
     * Пересчитывает оценку полезности отзыва.
     *
     * @param id     ID отзыва
     * @param userId ID юзера добавляющего/удаляющего оценку.
     * @param isAdd  Флаг t/f - добавление/удаление.
     * @param isLike Флаг t/t - лайк/дизлайк
     * @return Отзыв с обновленной оценкой.
     */
    public Review changeUsefulness(Long id, Long userId, boolean isAdd, boolean isLike) {
        Review review = reviewStorage.get(id);
        if (review == null) throw new ValidationNotFoundException(String
                .format("reviewId=%s не найден.", id));
        if (userStorage.get(userId) == null) throw new ValidationNotFoundException(String
                .format("userId=%s не найден.", userId));

        // k - величина изменения оценки.
        // Если раньше оценки не было, то он равен 1.
        // Если была противоположная, то равен 2.
        // oldIsLike значение предыдущей оценки.
        Boolean oldIsLike = reviewLikesStorage.getReviewLikeByUser(id, userId);
        int k = (isLike) ? 1 : -1;
        if (isAdd) {                                    // Добавление или изменение оценки.
            if (oldIsLike != null)                      // Если оценка уже есть:
                if (oldIsLike ^ isLike)                 // Сравниваем старую и новую оценки:
                    k *= 2;                             // Если противоположные, то k удваивается.
                else
                    return review;                      // Если одинаковые, то ничего не надо делать.
            else {                                      // Если раньше оценки не было:
                // NOP                                  // k остаётся равным 1.
            }
            reviewLikesStorage.put(id, userId, isLike);
        } else {                                        // Удаление оценки.
            if (oldIsLike == null) throw new ValidationNotFoundException(String
                    .format("Пользователь userID=%s не оценивал отзыв reviewID=%s.", userId, id));
            else if (oldIsLike ^ isLike) throw new ValidationNotFoundException(String
                    .format("Невозможно обработать запрос на удаление оценки отзыва " +
                            "reviewID=%s пользователем userID=%s. " +
                            "Несовпадение установленной и запрашиваемой оценок.", id, userId));
            k = -k; // Удаляем лайк - полезность уменьшается.
            reviewLikesStorage.remove(id, userId);
        }
        review.setUseful(review.getUseful() + k);
        return reviewStorage.update(review, 1);
    }

    void updateUsefulForRemoveUser(Long userId) {
        reviewStorage.getReviewListByUser(userId).forEach((review, isLike) -> {
            int k = (isLike) ? 1 : -1;
            review.setUseful(review.getUseful() - k);
            reviewStorage.update(review, 1);
        });
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
