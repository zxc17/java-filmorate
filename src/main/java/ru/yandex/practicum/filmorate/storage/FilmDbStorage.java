package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film f) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", f.getName());
        values.put("DESCRIPTION", f.getDescription());
        values.put("RELEASE_DATE", f.getReleaseDate());
        values.put("DURATION", f.getDuration());
        values.put("MPA_ID", f.getMpa().getId());
        KeyHolder keyHolder = jdbcInsert
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FilM_ID")
                .executeAndReturnKeyHolder(values);
        f.setId(keyHolder.getKey().longValue());
        return f;
    }

    @Override
    public Film get(long id) {
        String sql = "select * " +
                "from FILMS " +
                "join MPA on FILMS.MPA_ID = MPA.MPA_ID " +
                "where FILM_ID = ?";
        List<Film> query = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из БД FILMS, id=%s.", id));
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "select * " +
                "from FILMS join MPA on FILMS.MPA_ID = MPA.MPA_ID ";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film update(Film f) {
        String sql = "update FILMS " +
                "set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "where FILM_ID = ?";
        if (jdbcTemplate.update(sql,
                f.getName(), f.getDescription(), f.getReleaseDate(), f.getDuration(), f.getMpa().getId(),
                f.getId()) == 0)
            throw new StorageException(String.format("Ошибка при обновлении данных в БД FILMS, id=%s.", f.getId()));
        // Обновление жанров вызывается отдельно из модуля "сервис", обрабатывается FilmGenreStorage.
        return f;
    }


    @Override
    public void remove(long id) {
        String sql = "delete from FILMS where FILM_ID = ?";
        if (jdbcTemplate.update(sql, id) == 0)
            throw new StorageException(String.format("Ошибка при удалении из БД FILMS, id=%s.", id));
    }

    @Override
    public void clear() {
        String sql = "delete from FILMS";
        jdbcTemplate.update(sql);
    }


    private Film mapRowToFilm(ResultSet resultSet, int numRow) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(Mpa.builder()
                        .id(resultSet.getLong("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME"))
                        .build())
                .build();
    }
}
