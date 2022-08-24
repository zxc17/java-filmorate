package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film f);

    Film get(long id);

    List<Film> getAll();

    Film update(Film f);

    double updateRate(long id, double rate);

    void remove(long id);

    void clear();

    List<Film> getDirectorsFilmSortByLikes(long directorId);

    List<Film> getDirectorsFilmSortByYears(long directorId);

    List<Film> getPopularFilmsByGenre(Long genreId, Integer count);

    List<Film> getPopularFilmsByYear(Integer year, Integer count);

    List<Film> getPopularFilmsByYearAndGenre(Integer year, Long genreId, Integer count);

    List<Film> getCommonFilms(long user1Id, long user2Id);

    List<Film> searchFilmByTitle(String query);

    List<Film> searchFilmByDirector(String query);

    List<Film> searchFilmByTitleAndDirector(String query);

    List<Film> getPopularFilms(Integer count);

    double calculateRate(long id);

    List<Film> getRatedFilmListByUser(long userId);
}
