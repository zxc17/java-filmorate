package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUserList();

    User addUser(User u);

    User updateUser(User u);

    void clear();

    User getUser(long id);
}
