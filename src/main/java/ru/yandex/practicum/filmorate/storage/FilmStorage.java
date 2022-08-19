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

    List<Film> getPopularFilmsByGenre(Integer genreId, Integer count);

    List<Film> getPopularFilmsByYear(Integer year, Integer count);

    List<Film> getPopularFilmsByYearAndGenre(Integer year, Integer genreId, Integer count);

    List<Film> getCommonFilms(long user1Id, long user2Id);

    List<Film> searchFilmByTitle(String query);

    List<Film> searchFilmByDirector(String query);

}
