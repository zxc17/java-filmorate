package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface FilmGenreStorage {

    abstract void add(long filmId, long genreId);

    List<Long> get(long filmId);

    void update(long filmId, Set<Genre> genres);

    void remove(long filmId, long genreId);
}
