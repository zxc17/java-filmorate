package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public Review add(@RequestBody Review review) {
        log.info("Начато выполнение \"Добавить отзыв.\"");
        Review result = reviewService.add(review);
        log.info("Закончено выполнение \"Добавить отзыв.\"");
        return result;
    }

    @GetMapping("/reviews/{id}")
    public Review get(@PathVariable Long id) {
        log.info("Начато выполнение \"Получить отзыв по ID.\"");
        Review result = reviewService.get(id);
        log.info("Закончено выполнение \"Получить отзыв по ID.\"");
        return result;
    }

    @GetMapping("/reviews")
    public List<Review> getList(@RequestParam(required = false) Long filmId,
                                @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Начато выполнение \"Получить список отзывов фильма.\"");
        List<Review> result = reviewService.getList(filmId, count);
        log.info("Закончено выполнение \"Получить список отзывов фильма.\"");
        return result;
    }

    @PutMapping("/reviews")
    public Review update(@RequestBody Review review) {
        log.info("Начато выполнение \"Обновить отзыв.\"");
        Review result = reviewService.update(review);
        log.info("Закончено выполнение \"Обновить отзыв.\"");
        return result;
    }

    @DeleteMapping("/reviews/{id}")
    public void remove(@PathVariable Long id) {
        log.info("Начато выполнение \"Удалить отзыв по ID.\"");
        reviewService.remove(id);
        log.info("Закончено выполнение \"Удалить отзыв по ID.\"");
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public Review addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Добавить лайк отзыву.\"");
        Review result = reviewService.changeUseful(id, userId, true, true);
        log.info("Закончено выполнение \"Добавить лайк отзыву.\"");
        return result;
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public Review addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Добавить дизлайк отзыву.\"");
        Review result = reviewService.changeUseful(id, userId, true, false);
        log.info("Закончено выполнение \"Добавить дизлайк отзыву.\"");
        return result;
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public Review removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Удалить лайк отзыву.\"");
        Review result = reviewService.changeUseful(id, userId, false, true);
        log.info("Закончено выполнение \"Удалить лайк отзыву.\"");
        return result;
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Удалить дизлайк отзыву.\"");
        Review result = reviewService.changeUseful(id, userId, false, false);
        log.info("Закончено выполнение \"Удалить дизлайк отзыву.\"");
        return result;
    }
}
