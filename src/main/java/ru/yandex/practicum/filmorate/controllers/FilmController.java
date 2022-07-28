package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable long id) {
        return filmService.getFilm(id);
    }

    @GetMapping("/films")
    public List<Film> getFilmList() {
        return filmService.getFilmList();
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film f) {
        return filmService.addFilm(f);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film f) {
        return filmService.updateFilm(f);
    }

    @DeleteMapping("/films")
    public void clear() {
        filmService.clear();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilmList(@RequestParam(defaultValue = "10", required = false) long count) {
        return filmService.getPopularFilmList(count);
    }
}
