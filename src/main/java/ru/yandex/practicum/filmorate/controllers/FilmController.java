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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping("/films")
    public Film add(@RequestBody Film f) {
        log.info("Начато выполнение \"Добавить фильм\".");
        return filmService.add(f);
    }

    @GetMapping("/films/{id}")
    public Film get(@PathVariable long id) {
        log.info("Начато выполнение \"Получить фильм по ID\".");
        return filmService.get(id);
    }

    @GetMapping("/films")
    public List<Film> getAll() {
        log.info("Начато выполнение \"Получить все фильмы\".");
        return filmService.getAll();
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film f) {
        log.info("Начато выполнение \"Обновить фильм\".");
        return filmService.update(f);
    }

    @DeleteMapping("/films/{id}")
    public void remove(@PathVariable long id) {
        log.info("Начато выполнение \"Удалить фильм по ID\".");
        filmService.remove(id);
    }

    @DeleteMapping("/films")
    public void clear() {
        log.info("Начато выполнение \"Удалить все фильмы\".");
        filmService.clear();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void putLike(@PathVariable long id,
                        @PathVariable long userId,
                        @RequestParam() Integer rate) {
        log.info("Начато выполнение \"Добавить лайк фильму\".");
        filmService.putLike(id, userId, rate);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Начато выполнение \"Удалить лайк фильму\".");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilmList(@RequestParam(defaultValue = "10", required = false) Integer count,
                                         @RequestParam(required = false) Integer year,
                                         @RequestParam(required = false) Long genreId) {
        log.info("Начато выполнение \"Получить список популярных фильмов\".");
        return filmService.getPopularFilmList(count, year, genreId);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getSortedListByDirectors(
            @PathVariable long directorId,
            @RequestParam(value = "sortBy", required = false) String sort) {
        log.info("Начато выполнение \"Получить список фильмов, отсортированных по режиссеру\".");
        return filmService.getSortedListByDirectors(directorId, sort);
    }

    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam long userId,
                                     @RequestParam long friendId) {
        log.info("Начато выполнение \"Получить список фильмов с взаимными лайками\".");
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam @NotNull List<String> by) {
        log.info("Начато выполнение \"Поиск фильмов\".");
        return filmService.searchFilms(query, by);
    }
}
