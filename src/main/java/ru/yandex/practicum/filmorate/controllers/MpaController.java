package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/mpa/{id}")
    public Mpa get(@PathVariable long id) {
        log.info("Начато выполнение \"Получить рейтинг MPA.\"");
        Mpa result = mpaService.get(id);
        log.info("Закончено выполнение \"Получить рейтинг MPA.\"");
        return result;
    }

    @GetMapping("/mpa")
    public List<Mpa> getAll() {
        log.info("Начато выполнение \"Получить список рейтингов MPA.\"");
        List<Mpa> result = mpaService.getAll();
        log.info("Закончено выполнение \"Получить список рейтингов MPA.\"");
        return result;
    }

}
