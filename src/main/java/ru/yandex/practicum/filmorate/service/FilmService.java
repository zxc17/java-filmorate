package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final FilmGenreStorage filmGenreStorage;
    final LikesStorage likesStorage;
    final GenreStorage genreStorage;
    final MpaStorage mpaStorage;
    final FilmDirectorStorage filmDirectorStorage;
    final DirectorStorage directorStorage;
    final EventStorage eventStorage;

    public Film add(Film f) {
        if (!isValidFilm(f)) throw new ValidationDataException("Некорректные данные фильма.");
        // Чтобы в возврате было всё корректно, прописываем mpa_name
        if (f.getMpa().getName() == null)
            f.setMpa(mpaStorage.get(f.getMpa().getId()));
        filmStorage.add(f);
        if (f.getGenres() != null) {
            filmGenreStorage.update(f.getId(), f.getGenres());
            // Чтобы в возврате было всё корректно, прописываем genre_name
            f.setGenres(genreStorage.getNames(f.getGenres()));
        }
        if (f.getDirectors() != null) {
            filmDirectorStorage.updateDirectorByFilm(f.getId(), f.getDirectors());
            f.setDirectors(directorStorage.getNames(f.getDirectors()));
        }
        return f;
    }

    public Film get(long filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        List<Film> films = new ArrayList<>();
        films.add(film);
        loadDataIntoFilm(films);
        return films.get(0);
    }

    public List<Film> getAll() {
        List<Film> result = filmStorage.getAll();
        loadDataIntoFilm(result);
        return result;
    }

    public Film update(Film f) {
        if (!isValidFilm(f)) throw new ValidationDataException("Некорректные данные фильма.");
        if (filmStorage.get(f.getId()) == null) throw new ValidationNotFoundException(
                String.format("Невозможно обновить данные фильма, id=%s не найден.", f.getId()));
        // Чтобы в возврате было всё корректно, прописываем mpa_name
        if (f.getMpa().getName() == null)
            f.setMpa(mpaStorage.get(f.getMpa().getId()));
        filmStorage.update(f);
        if (f.getGenres() != null)
            filmGenreStorage.update(f.getId(), f.getGenres());
        if (f.getDirectors() != null) {
            filmDirectorStorage.updateDirectorByFilm(f.getId(), f.getDirectors());
        } else {
            filmDirectorStorage.clearDirectorByFilm(f.getId());
        }
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

    public void addLike(long filmId, long userId) {
        if (filmStorage.get(filmId) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        likesStorage.add(filmId, userId);
        eventStorage.addEvent(userId, filmId, "LIKE", "ADD");
    }

    public long getLikeCount(long id) {
        return likesStorage.get(id).size();
    }

    public void removeLike(long filmId, long userId) {
        if (filmStorage.get(filmId) == null)
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        if (userStorage.get(userId) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        likesStorage.remove(filmId, userId);
        eventStorage.addEvent(userId, filmId, "LIKE", "REMOVE");
    }

    public List<Film> getPopularFilmList(Integer count, Integer year, Integer genreId) {
        List<Film> filmList = null;

        if (year != null && genreId == null) {
            filmList = filmStorage.getPopularFilmsByYear(year, count);
        } else if (genreId != null && year == null) {
            filmList = filmStorage.getPopularFilmsByGenre(genreId, count);
        } else if (genreId != null && year != null) {
            filmList = filmStorage.getPopularFilmsByYearAndGenre(year, genreId, count);
        } else {
            filmList = filmStorage.getAll().stream()
                    .sorted(Comparator.comparing((Film f) -> getLikeCount(f.getId())).reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        }

        loadDataIntoFilm(filmList);

        return filmList;
    }

    public List<Film> getSortedListByDirectors(long directorId, String sort) {
        if (sort.equals("year")) {
            List<Film> result = new ArrayList<>(filmStorage.getDirectorsFilmSortByYears(directorId));
            if (result.size() == 0) {
                throw new ValidationNotFoundException(String.format("directorId=%s не найден.", directorId));
            }
            loadDataIntoFilm(result);
            return result;
        } else if (sort.equals("likes")) {
            return getFilmsSortByLikes(directorId);
        } else {
            throw new ValidationNotFoundException(
                    String.format("Сортирока может быть только по year и likes. Указана сортировка = %s", sort));
        }
    }

    public List<Film> getCommonFilms(long user1Id, long user2Id) {
        if (userStorage.get(user1Id) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", user1Id));
        if (userStorage.get(user2Id) == null)
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", user2Id));
        List<Film> result = filmStorage.getCommonFilms(user1Id, user2Id);
        loadDataIntoFilm(result);
        return result;
    }

    private List<Film> getFilmsSortByLikes(long directorId) {
        List<Film> result = filmStorage.getDirectorsFilm(directorId).stream()
                .sorted(Comparator.comparing((Film f) -> getLikeCount(f.getId())).reversed())
                .collect(Collectors.toList());
        if (result.size() == 0) {
            throw new ValidationNotFoundException(String.format("directorId=%s не найден.", directorId));
        }
        loadDataIntoFilm(result);
        return result;
    }

    private void loadDataIntoFilm(List<Film> films) {
        films.forEach(film -> {
            film.setGenres(filmGenreStorage.get(film.getId()).stream()
                    .map(genreStorage::get)
                    .collect(Collectors.toSet()));
            film.setLikes(likesStorage.get(film.getId()));
            film.setDirectors(filmDirectorStorage.getDirectorByFilm(film.getId()).stream()
                    .map(directorStorage::getById)
                    .collect(Collectors.toSet()));
        });
    }

    private boolean isValidFilm(Film f) {
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
            return false;
        else
            return true;
    }
}
