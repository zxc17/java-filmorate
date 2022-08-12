package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
}
