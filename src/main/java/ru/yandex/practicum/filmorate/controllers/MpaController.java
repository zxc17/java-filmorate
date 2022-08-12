package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@AllArgsConstructor
public class MpaController {
    final MpaService mpaService;

    @GetMapping("/mpa/{id}")
    public Mpa get(@PathVariable long id) {
        return mpaService.get(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAll() {
        return mpaService.getAll();
    }

}
