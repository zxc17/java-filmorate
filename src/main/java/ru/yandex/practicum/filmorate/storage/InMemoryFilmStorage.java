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
    public List<Film> getFilmList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film f) {
        f.setId(id++);
        films.put(f.getId(), f);
        log.info("Фильм id={} успешно добавлен.", f.getId());
        return f;
    }

    @Override
    public Film updateFilm(Film f) {
        films.put(f.getId(), f);
        log.info("Данные фильма id={} успешно обновлены.", f.getId());
        return f;
    }

    @Override
    public void clear() {
        films.clear();
        id = 1;
    }

    @Override
    public Film getFilm(long id) {
        return films.get(id);
    }


}
