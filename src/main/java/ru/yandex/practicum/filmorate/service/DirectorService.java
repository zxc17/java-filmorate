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

    public Director add(Director d) {
        if (d.getName() == null || d.getName().isBlank()) {
            throw new ValidationDataException("Некорректные данные режиссера.");
        }
        return directorStorage.add(d);
    }

    public Set<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director getById(long id) {
        Director result = directorStorage.getById(id);
        if (result == null) throw new ValidationNotFoundException(String.format("directorId=%s не найден.", id));
        return result;
    }

    public Director update(Director d) {
        if (d.getName() == null || d.getName().isBlank()) {
            throw new ValidationDataException("Некорректные данные режиссера.");
        }
        if (directorStorage.update(d) == 0) throw new ValidationNotFoundException(
                String.format("Невозможно обновить данные режиссера, id=%s не найден.", d.getId()));
        return d;
    }

    public void remove(long id) {
        if (directorStorage.remove(id) == 0)
            throw new ValidationNotFoundException(
                    String.format("Ошибка при удалении данных из БД. directorId=%s не найден.", id));
    }

}
