package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface LikesStorage {

    void add(long filmId, long userId);

    Set<Long> get(long filmId);

    void remove(long filmId, long userId);
}
