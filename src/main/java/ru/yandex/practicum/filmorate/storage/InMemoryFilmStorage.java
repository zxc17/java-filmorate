package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 1;

    @Override
    public Film add(Film f) {
        f.setId(id++);
        films.put(f.getId(), f);
        log.info("Фильм id={} успешно добавлен.", f.getId());
        return f;
    }

    @Override
    public Film update(Film f) {
        films.put(f.getId(), f);
        log.info("Данные фильма id={} успешно обновлены.", f.getId());
        return f;
    }

    @Override
    public void remove(long id) {
        films.remove(id);
        log.info("Фильм id={} успешно удален.", id);
    }

    @Override
    public List<Film> getList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void clear() {
        films.clear();
        id = 1;
    }

    @Override
    public Film get(long id) {
        return films.get(id);
    }


}
