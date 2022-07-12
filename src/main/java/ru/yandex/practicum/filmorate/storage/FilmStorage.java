package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film update(Film f);

    Film add(Film f);

    Film get(long id);

    void remove(long id);
    List<Film> getList();

    void clear();
}
