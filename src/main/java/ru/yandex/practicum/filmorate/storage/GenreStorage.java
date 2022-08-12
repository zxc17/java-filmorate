package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface GenreStorage {

    Genre get(long id);

    Set<Genre> getAll();

    Set<Genre> getNames(Set<Genre> genresIds);

}
