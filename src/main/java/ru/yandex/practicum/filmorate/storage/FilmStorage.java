package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film add(Film f);

    Film get(long id);

    List<Film> getAll();

    Film update(Film f);

    void remove(long id);

    void clear();

    Collection<Film> getDirectorsFilm(long directorId);

    Collection<Film> getDirectorsFilmSortByYears(long directorId);

}
