package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/directors")
    public Set<Director> getAll() {
        log.info("Начато выполнение \"Получить всех всех режиссеров.\"");
        Set<Director> result = directorService.getAll();
        log.info("Закончено выполнение \"Получить всех всех режиссеров.\"");
        return result;
    }

    @GetMapping("/directors/{id}")
    public Director get(@PathVariable long id) {
        log.info("Начато выполнение \"Получить режиссера по ID.\"");
        Director result = directorService.getById(id);
        log.info("Закончено выполнение \"Получить режиссера по ID.\"");
        return result;
    }

    @PostMapping("/directors")
    public Director add(@RequestBody Director d) {
        log.info("Начато выполнение \"Добавить режиссера.\"");
        Director result = directorService.add(d);
        log.info("Закончено выполнение \"Добавить режиссера.\"");
        return result;
    }

    @PutMapping("/directors")
    public Director update(@RequestBody Director d) {
        log.info("Начато выполнение \"Обновить режиссера.\"");
        Director result = directorService.update(d);
        log.info("Закончено выполнение \"Обновить режиссера.\"");
        return result;
    }

    @DeleteMapping("/directors/{id}")
    public void remove(@PathVariable long id) {
        log.info("Начато выполнение \"Удалить режиссера.\"");
        directorService.remove(id);
        log.info("Закончено выполнение \"Удалить режиссера.\"");
    }

}
