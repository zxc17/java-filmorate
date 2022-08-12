package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GenreController {
    final GenreService genreService;

    @GetMapping("/genres/{id}")
    public Genre get(@PathVariable long id) {
        return genreService.get(id);
    }

    @GetMapping("/genres")
    public Set<Genre> getAll() {
        return genreService.getAll();
    }

}
