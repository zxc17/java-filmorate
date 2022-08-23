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
        return reviewService.add(review);
    }

    @GetMapping("/reviews/{id}")
    public Review get(@PathVariable Long id) {
        log.info("Начато выполнение \"Получить отзыв по ID.\"");
        return reviewService.get(id);
    }

    @GetMapping("/reviews")
    public List<Review> getList(@RequestParam(required = false) Long filmId,
                                @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Начато выполнение \"Получить список отзывов фильма.\"");
        return reviewService.getList(filmId, count);
    }

    @PutMapping("/reviews")
    public Review update(@RequestBody Review review) {
        log.info("Начато выполнение \"Обновить отзыв.\"");
        return reviewService.update(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void remove(@PathVariable Long id) {
        log.info("Начато выполнение \"Удалить отзыв по ID.\"");
        reviewService.remove(id);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public Review addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Добавить лайк отзыву.\"");
        return reviewService.changeUseful(id, userId, true, true);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public Review addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Добавить дизлайк отзыву.\"");
        return reviewService.changeUseful(id, userId, true, false);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public Review removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Удалить лайк отзыву.\"");
        return reviewService.changeUseful(id, userId, false, true);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Начато выполнение \"Удалить дизлайк отзыву.\"");
        return reviewService.changeUseful(id, userId, false, false);
    }
}
