package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User u);

    User update(User u);

    User get(long id);

    void remove(long id);

    List<User> getList();

    void clear();
}
