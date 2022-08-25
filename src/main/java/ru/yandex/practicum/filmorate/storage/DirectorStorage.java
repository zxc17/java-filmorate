package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface DirectorStorage {

    Director getById(long id);

    Set<Director> getAll();

    Director add(Director d);

    int update(Director d);

    int remove(long id);

    Set<Director> getNames(Set<Director> directorsIds);

}
