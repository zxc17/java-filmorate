package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface FilmDirectorStorage {

    List<Long> getDirectorByFilm(long filmId);

    void updateDirectorByFilm(long filmId, Set<Director> directors);

    void clearDirectorByFilm(long filmId);

}
