package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface DirectorStorage {

    Director getDirectorById(long id);

    Set<Director> getAllDirectors();

    Director addDirector(Director d);

    int updateDirector(Director d);

    int deleteDirector(long id);

    Set<Director> getNames(Set<Director> directorsIds);

}
