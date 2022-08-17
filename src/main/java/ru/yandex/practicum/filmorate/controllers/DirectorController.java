package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
public class DirectorController {
    DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/directors")
    public Set<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director get(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director add(@RequestBody Director d) {
        return directorService.addDirector(d);
    }

    @PutMapping("/directors")
    public Director update(@RequestBody Director d) {
        return directorService.updateDirector(d);
    }

    @DeleteMapping("/directors/{id}")
    public void deleteDirector(@PathVariable long id) {
        directorService.deleteDirector(id);
    }

}
