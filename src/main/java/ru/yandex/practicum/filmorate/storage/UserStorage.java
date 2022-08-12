package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User u);

    User get(long id);

    List<User> getAll();

    User update(User u);

    void remove(long id);

    void clear();
}
