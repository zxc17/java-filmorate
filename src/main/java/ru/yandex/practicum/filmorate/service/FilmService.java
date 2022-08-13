package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film get(long filmId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            log.warn("filmId={} не найден.", filmId);
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        }
        return film;
    }

    public Film add(Film f) {
        if (!check(f)) {
            log.warn("Некорректные данные фильма.");
            throw new ValidationDataException("Некорректные данные фильма.");
        }
        return filmStorage.add(f);
    }

    public Film update(Film f) {
        if (!check(f)) {
            log.warn("Некорректные данные фильма.");
            throw new ValidationDataException("Некорректные данные фильма.");
        }
        if (filmStorage.get(f.getId()) == null) {
            log.warn("Невозможно обновить данные фильма, id={} не найден.", f.getId());
            throw new ValidationNotFoundException(
                    String.format("Невозможно обновить данные фильма, id=%s не найден.", f.getId()));
        }
        return filmStorage.update(f);
    }

    public void remove(long id) {
        if (filmStorage.get(id) == null) {
            log.warn("filmId={} не найден.", id);
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", id));
        }
        filmStorage.remove(id);
    }

    public List<Film> getList() {
        return filmStorage.getList();
    }

    public void clear() {
        filmStorage.clear();
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            log.warn("filmId={} не найден.", filmId);
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        }
        if (userStorage.get(userId) == null) {
            log.warn("userId={} не найден.", userId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.get(filmId);
        if (film == null) {
            log.warn("filmId={} не найден.", filmId);
            throw new ValidationNotFoundException(String.format("filmId=%s не найден.", filmId));
        }
        if (!film.getLikes().remove(userId)) {
            log.warn("userId={} не найден.", filmId);
            throw new ValidationNotFoundException(String.format("userId=%s не найден.", userId));
        }
    }

    public long getLikeCount(long id) {
        return filmStorage.get(id).getLikes().size();
    }

    public List<Film> getPopularFilmList(long count) {
        return filmStorage.getList().stream()
                .sorted(Comparator.comparing((Film f) -> getLikeCount(f.getId())).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean check(Film f) {
        if (f == null ||
                f.getName() == null ||
                f.getDescription() == null ||
                f.getReleaseDate() == null ||
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
