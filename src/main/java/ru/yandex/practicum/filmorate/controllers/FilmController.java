package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping("/films")
    public List<Film> getFilm() {
        return new ArrayList<>(films.values());
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film f) {
        if (!checkFilm(f)) {
            log.warn("Некорректные данные фильма.");
            throw new ValidationException("Некорректные данные фильма.");
        }
        f.setId(id++);
        films.put(f.getId(), f);
        log.info("Фильм id={} успешно добавлен.", f.getId());
        return f;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film f) {
        if (!checkFilm(f)) {
            log.warn("Некорректные данные фильма.");
            throw new ValidationException("Некорректные данные фильма.");
        }
        if (!films.containsKey(f.getId())) {
            log.warn("Невозможно обновить данные фильма, ID не найден.");
            throw new ValidationException("Невозможно обновить данные фильма, ID не найден.");
        }
        films.put(f.getId(), f);
        log.info("Данные фильма id={} успешно обновлены.", f.getId());
        return f;
    }

    @DeleteMapping("/films")
    public void clear() {
        films.clear();
        id = 1;
    }

    private boolean checkFilm(Film f) {
        if (f == null ||
                f.getName() == null ||
                f.getDescription() == null ||
                f.getReleaseDate() == null ||
                f.getName().isBlank() ||
                f.getDescription().length() > 200 ||
                f.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                f.getDuration() <= 0
        )
            return false;
        else
            return true;
    }
}
