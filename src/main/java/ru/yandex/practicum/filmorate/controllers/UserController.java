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
        User result = userService.add(u);
        log.info("Закончено выполнение \"Добавить пользователя.\"");
        return result;
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable long id) {
        log.info("Начато выполнение \"Получить пользователя по ID.\"");
        User result = userService.get(id);
        log.info("Закончено выполнение \"Получить пользователя по ID.\"");
        return result;
    }

    @GetMapping("/users")
    public List<User> getList() {
        log.info("Начато выполнение \"Получить список всех пользователей.\"");
        List<User> result = userService.getAll();
        log.info("Закончено выполнение \"Получить список всех пользователей.\"");
        return result;
    }

    @PutMapping("/users")
    public User update(@RequestBody User u) {
        log.info("Начато выполнение \"Обновить пользователя.\"");
        User result = userService.update(u);
        log.info("Закончено выполнение \"Обновить пользователя.\"");
        return result;
    }

    @DeleteMapping(("/users/{id}"))
    public void remove(@PathVariable long id) {
        log.info("Начато выполнение \"Удалить пользователя.\"");
        userService.remove(id);
        log.info("Закончено выполнение \"Удалить пользователя.\"");
    }

    @DeleteMapping("/users")
    public void clear() {
        log.info("Начато выполнение \"Удалить всех пользователей.\"");
        userService.clear();
        log.info("Закончено выполнение \"Удалить всех пользователей.\"");
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Начато выполнение \"Добавить друга.\"");
        userService.addFriend(id, friendId);
        log.info("Закончено выполнение \"Добавить друга.\"");
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Начато выполнение \"Удалить друга.\"");
        userService.removeFriend(id, friendId);
        log.info("Закончено выполнение \"Удалить друга.\"");
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendList(@PathVariable long id) {
        log.info("Начато выполнение \"Получить список друзей.\"");
        List<User> result = userService.getFriendList(id);
        log.info("Закончено выполнение \"Получить список друзей.\"");
        return result;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendList(@PathVariable long id, @PathVariable long otherId) {
        log.info("Начато выполнение \"Получить список общих друзей.\"");
        List<User> result = userService.getCommonFriendList(id, otherId);
        log.info("Закончено выполнение \"Получить список общих друзей.\"");
        return result;
    }

    @GetMapping("/users/{id}/feed")
    public List<Event> getFeed(@PathVariable long id) {
        log.info("Начато выполнение \"Получить ленту событий пользователя.\"");
        List<Event> result = userService.getFeedForUser(id);
        log.info("Закончено выполнение \"Получить ленту событий пользователя.\"");
        return result;
    }

    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable long id) {
        log.info("Начато выполнение \"Получить список рекомендаций.\"");
        List<Film> result = userService.getRecommendation(id);
        log.info("Закончено выполнение \"Получить список рекомендаций.\"");
        return result;
    }
}
