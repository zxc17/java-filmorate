package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
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
        return f;
    }

    public Film get(long filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        film.setGenres(filmGenreStorage.get(filmId).stream()
                .map(genreStorage::get)
                .collect(Collectors.toSet()));
        film.setLikes(likesStorage.get(filmId));
        return film;
    }

    public List<Film> getAll() {
        List<Film> result = filmStorage.getAll();
        result.forEach(film -> {
            film.setGenres(filmGenreStorage.get(film.getId()).stream()
                    .map(genreStorage::get)
                    .collect(Collectors.toSet()));
            film.setLikes(likesStorage.get(film.getId()));
        });
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
    }

    public List<Film> getPopularFilmList(long count) {
        List<Film> result = filmStorage.getAll().stream()
                .sorted(Comparator.comparing((Film f) -> getLikeCount(f.getId())).reversed())
                .limit(count)
                .collect(Collectors.toList());
        result.forEach(film -> {
            film.setGenres(filmGenreStorage.get(film.getId()).stream()
                    .map(genreStorage::get)
                    .collect(Collectors.toSet()));
            film.setLikes(likesStorage.get(film.getId()));
        });
        return result;
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
