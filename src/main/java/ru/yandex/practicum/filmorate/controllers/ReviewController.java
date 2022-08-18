package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review add(@RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        return reviewService.update(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.changeUseful(id, userId, true, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDisLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.changeUseful(id, userId, true, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.changeUseful(id, userId, false, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewService.changeUseful(id, userId, false, false);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        reviewService.remove(id);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable Long id) {
        return reviewService.get(id);
    }

    @GetMapping
    public List<Review> getList(@RequestParam(required = false) Long filmId,
                                @RequestParam(required = false) Integer count) {
        if (filmId == null || filmId < 0)
            filmId = -1L;
        if (count == null)
            count = -1;
        return reviewService.getList(filmId, count);
    }
}
