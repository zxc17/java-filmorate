package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@RestController
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films/{id}")
    public Film get(@PathVariable long id) {
        return filmService.get(id);
    }

    @GetMapping("/films")
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @PostMapping("/films")
    public Film add(@RequestBody Film f) {
        return filmService.add(f);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film f) {
        return filmService.update(f);
    }

    @DeleteMapping("/films/{id}")
    public void remove(@PathVariable long id) {
        filmService.remove(id);
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

    @GetMapping("/films/director/{directorId}")
    public List<Film> getSortedListByDirectors(
            @PathVariable long directorId,
            @RequestParam(value = "sortBy", required = false) String sort) {
        return filmService.getSortedListByDirectors(directorId, sort);

    }

}
