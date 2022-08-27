package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Rate;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;

    public Film add(Film f) {
        if (isInvalidFilm(f)) throw new ValidationDataException("Некорректные данные фильма.");
        filmStorage.add(f);
        storeAdditionalFields(f);
        // Чтобы в возврате было всё корректно, прописываем mpa_name, genre_name, director_name
        loadDataIntoFilm(f);
        return f;
    }

    public Film get(long filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        loadDataIntoFilm(film);
        return film;
    }

    public List<Film> getAll() {
        List<Film> result = filmStorage.getAll();
        loadDataIntoFilms(result);
        return result;
    }

    public Film update(Film f) {
        if (isInvalidFilm(f)) throw new ValidationDataException("Некорректные данные фильма.");
        if (filmStorage.get(f.getId()) == null) throw new ValidationNotFoundException(
                String.format("Невозможно обновить данные фильма, id=%s не найден.", f.getId()));
        filmStorage.update(f);
        storeAdditionalFields(f);
        // Чтобы в возврате было всё корректно, прописываем mpa_name, genre_name, director_name
        loadDataIntoFilm(f);
        return f;
    }

    public void remove(long id) {
        if (filmStorage.get(id) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", id));
        filmStorage.remove(id);
    }

    public void clear() {
        filmStorage.clear();
    }

    public void putLike(long filmId, long userId, Integer rate) {
        if (filmStorage.get(filmId) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        if (isInvalidRate(Double.valueOf(rate)))
            throw new ValidationNotFoundException(String.format("Некорректное значение rate=%s.", rate));
        likesStorage.put(filmId, userId, rate);
        filmStorage.updateRate(filmId, filmStorage.calculateRate(filmId));
        eventStorage.addEvent(userId, filmId, EventType.LIKE, OperationType.ADD);
    }

    public void removeLike(long filmId, long userId) {
        if (filmStorage.get(filmId) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        likesStorage.remove(filmId, userId);
        filmStorage.updateRate(filmId, filmStorage.calculateRate(filmId));
        eventStorage.addEvent(userId, filmId, EventType.LIKE, OperationType.REMOVE);
    }

    public List<Film> getPopularFilmList(Integer count, Integer year, Long genreId) {
        List<Film> filmList;
        if (count <= 0) throw new ValidationDataException(String
                .format("Недопустимон значение count=%d.", count));

        // Фильтр по годам.
        if (year != null && genreId == null) {
            if (year <= 0) throw new ValidationDataException(String
                    .format("Недопустимое значение year=%d.", year));
            filmList = filmStorage.getPopularFilmsByYear(year, count);
            // Фильтр по жанрам.
        } else if (genreId != null && year == null) {
            if (genreStorage.get(genreId) == null) throw new ValidationNotFoundException(String
                    .format("genreId=%s не найден.", genreId));
            filmList = filmStorage.getPopularFilmsByGenre(genreId, count);
            // Фильтр по годам и жанрам.
        } else if (genreId != null && year != null) {
            if (year <= 0) throw new ValidationDataException(String
                    .format("Недопустимое значение year=%d.", year));
            if (genreStorage.get(genreId) == null) throw new ValidationNotFoundException(String
                    .format("genreId=%s не найден.", genreId));
            filmList = filmStorage.getPopularFilmsByYearAndGenre(year, genreId, count);
            // Без фильтра.
        } else {
            filmList = filmStorage.getPopularFilms(count);
        }

        loadDataIntoFilms(filmList);
        return filmList;
    }

    public List<Film> getSortedListByDirectors(long directorId, String sort) {
        if (directorStorage.getById(directorId) == null) throw new ValidationNotFoundException(String
                .format("directorId=%s не найден.", directorId));
        List<Film> result;

        if (sort.equals("year")) {
            result = filmStorage.getDirectorsFilmSortByYears(directorId);
        } else if (sort.equals("likes")) {
            result = filmStorage.getDirectorsFilmSortByLikes(directorId);
        } else {
            throw new ValidationDataException(String
                    .format("Сортирока может быть только по year и likes. Указана сортировка = %s", sort));
        }
        loadDataIntoFilms(result);
        return result;
    }

    public List<Film> getCommonFilms(long user1Id, long user2Id) {
        if (userStorage.get(user1Id) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", user1Id));
        if (userStorage.get(user2Id) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", user2Id));
        List<Film> result = filmStorage.getCommonFilms(user1Id, user2Id);
        loadDataIntoFilms(result);
        return result;
    }

    public List<Film> searchFilms(String query, List<String> by) {
        List<String> correctKey = List.of("director", "title");
        by.forEach(k -> {
            if (!correctKey.contains(k)) throw new ValidationDataException(String
                    .format("Некорректный ключ поиска key=%s", k));
        });
        List<Film> filmList = new ArrayList<>();

        if (by.size() == 2) {
            filmList = filmStorage.searchFilmByTitleAndDirector(query);
        } else if (by.get(0).contains("director")) {
            filmList = filmStorage.searchFilmByDirector(query);
        } else if (by.get(0).contains("title")) {
            filmList = filmStorage.searchFilmByTitle(query);
        }

        loadDataIntoFilms(filmList);
        return filmList;
    }

    void updateRateForFilmList(List<Long> filmIds) {
        filmIds.forEach(filmId -> filmStorage.updateRate(filmId, filmStorage.calculateRate(filmId)));
    }

    void loadDataIntoFilm(Film f) {
        if (f.getMpa().getName() == null)
            f.setMpa(mpaStorage.get(f.getMpa().getId()));
        f.setGenres(filmGenreStorage.get(f.getId()).stream()
                .map(genreStorage::get)
                .collect(Collectors.toSet()));
        f.setLikes(likesStorage.get(f.getId()));
        f.setDirectors(filmDirectorStorage.getDirectorByFilm(f.getId()).stream()
                .map(directorStorage::getById)
                .collect(Collectors.toSet()));
    }

    void loadDataIntoFilms(List<Film> films) {
        films.forEach(this::loadDataIntoFilm);
    }

    private void storeAdditionalFields(Film f) {
        if (f.getGenres() != null)
            filmGenreStorage.update(f.getId(), f.getGenres());
        if (f.getLikes() != null) {
            double rate = f.getLikes().stream().mapToDouble(Rate::getRate)
                    .average().orElse(0.0);
            f.setRate(rate);
            likesStorage.update(f.getId(), f.getLikes());
            filmStorage.updateRate(f.getId(), rate);
        }
        if (f.getDirectors() != null) {
            filmDirectorStorage.updateDirectorByFilm(f.getId(), f.getDirectors());
        } else {
            filmDirectorStorage.clearDirectorByFilm(f.getId());
        }
    }

    private boolean isInvalidFilm(Film f) {
        if (f == null ||
                f.getName() == null ||
                f.getDescription() == null ||
                f.getReleaseDate() == null ||
                f.getMpa() == null ||
                f.getMpa().getId() <= 0 ||
                f.getName().isBlank() ||
                f.getDescription().length() > 200 ||
                f.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                f.getDuration() <= 0
        )
            return true;
        else if (f.getLikes() != null) {
            return f.getLikes().stream().anyMatch(r -> isInvalidRate(r.getRate()));
        }
        return false;
    }

    private boolean isInvalidRate(Double rate) {
        return  (rate == null || rate < 1 || rate > 10);
    }
}
