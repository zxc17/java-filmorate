package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilmList();

    Film updateFilm(Film f);

    Film addFilm(Film f);

    void clear();

    Film getFilm(long id);
}
