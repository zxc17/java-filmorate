package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.customExceptions.ValidationNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    final MpaDbStorage mpaDbStorage;

    public Mpa get(long id) {
        Mpa result = mpaDbStorage.get(id);
        if (result == null) throw new ValidationNotFoundException(String.format("mpaID=%s не найден.", id));
        return result;
    }

    public List<Mpa> getAll() {
        return mpaDbStorage.getAll();
    }
}
