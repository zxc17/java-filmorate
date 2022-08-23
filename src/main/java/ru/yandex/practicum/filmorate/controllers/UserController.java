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
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public User add(@RequestBody User u) {
        log.info("Начато выполнение \"Добавить пользователя.\"");
        return userService.add(u);
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable long id) {
        log.info("Начато выполнение \"Получить пользователя по ID.\"");
        return userService.get(id);
    }

    @GetMapping("/users")
    public List<User> getList() {
        log.info("Начато выполнение \"Получить список всех пользователей.\"");
        return userService.getAll();
    }

    @PutMapping("/users")
    public User update(@RequestBody User u) {
        log.info("Начато выполнение \"Обновить пользователя.\"");
        return userService.update(u);
    }

    @DeleteMapping(("/users/{id}"))
    public void remove(@PathVariable long id) {
        log.info("Начато выполнение \"Удалить пользователя.\"");
        userService.remove(id);
    }

    @DeleteMapping("/users")
    public void clear() {
        log.info("Начато выполнение \"Удалить всех пользователей.\"");
        userService.clear();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Начато выполнение \"Добавить друга.\"");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Начато выполнение \"Удалить друга.\"");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendList(@PathVariable long id) {
        log.info("Начато выполнение \"Получить список друзей.\"");
        return userService.getFriendList(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendList(@PathVariable long id, @PathVariable long otherId) {
        log.info("Начато выполнение \"Получить список общих друзей.\"");
        return userService.getCommonFriendList(id, otherId);
    }

    @GetMapping("/users/{id}/feed")
    public List<Event> getFeed(@PathVariable long id) {
        log.info("Начато выполнение \"Получить ленту событий пользователя.\"");
        return userService.getFeedForUser(id);
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable long id) {
        log.info("Начато выполнение \"Получить список рекомендаций.\"");
        return userService.getRecommendation(id);
    }
}
