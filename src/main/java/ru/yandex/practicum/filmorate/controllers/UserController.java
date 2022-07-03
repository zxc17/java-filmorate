package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.customExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping("/users")
    public List<User> getUser(){
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User u) {
        if (!checkUser(u)) {
            log.warn("Некорректные данные пользователя.");
            throw new ValidationException("Некорректные данные пользователя.");
        }
        u.setId(id++);
        users.put(u.getId(), u);
        log.info("Пользователь id={} успешно добавлен.", u.getId());
        return u;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User u) {
        if (!checkUser(u)) {
            log.warn("Некорректные данные пользователя.");
            throw new ValidationException("Некорректные данные пользователя.");
        }
        if (!users.containsKey(u.getId())) {
            log.warn("Невозможно обновить данные пользователя, ID не найден.");
            throw new ValidationException("Невозможно обновить данные пользователя, ID не найден.");
        }
        users.put(u.getId(), u);
        log.info("Данные пользователя id={} успешно обновлены.", u.getId());
        return u;
    }

    @DeleteMapping("/users")
    public void clear() {
        users.clear();
        id = 1;
    }

    private boolean checkUser(User u) {
        if (u == null ||
                u.getLogin() == null ||
                u.getEmail() == null ||
                u.getBirthday() == null ||
                u.getEmail().isBlank() || !u.getEmail().contains("@") ||
                u.getLogin().isBlank() || u.getLogin().contains(" ") ||
                u.getBirthday().isAfter(LocalDate.now())
        )
            return false;
        else {
            if (u. getName() == null || u.getName().isBlank())
                u.setName(u.getLogin());
            return true;
        }
    }
}
