package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.customExceptions.ValidationDataException;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director addDirector(Director d) {
        if (d.getName() == null || d.getName().isBlank()) {
            throw new ValidationDataException("Некорректные данные режиссера.");
        }
        return directorStorage.addDirector(d);
    }

    public Set<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(long id) {
        Director result = directorStorage.getDirectorById(id);
        if (result == null) throw new ValidationNotFoundException(String.format("directorId=%s не найден.", id));
        return result;
    }

    public Director updateDirector(Director d) {
        if (d.getName() == null || d.getName().isBlank()) {
            throw new ValidationDataException("Некорректные данные режиссера.");
        }
        if (directorStorage.updateDirector(d) == 0) throw new ValidationNotFoundException(
                String.format("Невозможно обновить данные режиссера, id=%s не найден.", d.getId()));
        return d;
    }

    public void deleteDirector(long id) {
        if (directorStorage.deleteDirector(id) == 0)
            throw new ValidationNotFoundException(
                    String.format("Ошибка при удалении данных из БД. directorId=%s не найден.", id));
    }

}
