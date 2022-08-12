package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    final GenreDbStorage genreDbStorage;

    public Genre get(long id) {
        Genre result = genreDbStorage.get(id);
        if (result == null) throw new ValidationNotFoundException(String.format("genreID=%s не найден.", id));
        return result;
    }

    public Set<Genre> getAll() {
        return genreDbStorage.getAll();
    }
}
