package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Set;

public interface LikesStorage {

    void add(long filmId, long userId);

    Set<Long> get(long filmId);

    /**
     * @param userId UserID
     * @return Мапа <filmID, like> фильмов-оценок указанного юзера, включая неоцененные.
     */
    HashMap<Film, Double> getLikeListByUser(long userId);

    void remove(long filmId, long userId);
}
