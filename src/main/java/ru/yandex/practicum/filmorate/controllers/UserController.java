package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User add(@RequestBody User u) {
        return userService.add(u);
    }

    @PutMapping("/users")
    public User update(@RequestBody User u) {
        return userService.update(u);
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable long id) {
        return userService.get(id);
    }

    // Задача по удалению фильмов и юзеров выполнена в предыдущем спринте. Комментарий, чтобы было что коммитить.
    @DeleteMapping(("/users/{id}"))
    public void remove(@PathVariable long id) {
        userService.remove(id);
    }

    @GetMapping("/users")
    public List<User> getList() {
        return userService.getAll();
    }

    @DeleteMapping("/users")
    public void clear() {
        userService.clear();
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendList(@PathVariable long id) {
        return userService.getFriendList(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendList(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriendList(id, otherId);
    }

    @GetMapping("/users/{id}/feed")
    public List<Event> getFeed(@PathVariable long id) {
        return userService.getFeedForUser(id);
    }
}
