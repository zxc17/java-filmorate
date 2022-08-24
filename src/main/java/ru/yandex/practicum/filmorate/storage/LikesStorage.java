package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rate;

import java.util.List;
import java.util.Set;

public interface LikesStorage {

    void put(long filmId, long userId, int rate);

    Set<Rate> get(long filmId);

    void update(long filmId, Set<Rate> likes);

    void remove(long filmId, long userId);

    /**
     * Возвращает список id фильмов, оцененных пользователем.
     *
     * @param userId ID пользователя
     */
    List<Long> getIdFilmsRatedByUser(long userId);
}
