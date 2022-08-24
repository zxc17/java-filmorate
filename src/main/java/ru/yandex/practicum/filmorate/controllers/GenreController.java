package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres/{id}")
    public Genre get(@PathVariable long id) {
        log.info("Начато выполнение \"Получить жанр\".");
        return genreService.get(id);
    }

    @GetMapping("/genres")
    public Set<Genre> getAll() {
        log.info("Начато выполнение \"Получить все жанры\".");
        return genreService.getAll();
    }

}
