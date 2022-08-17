package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director getDirectorById(long id) {
        String sql = "select * from DIRECTORS where DIRECTOR_ID = ?";
        List<Director> query = jdbcTemplate.query(sql, this::mapRowToDirector, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из БД DIRECTOR, id=%s.", id));
        }
    }

    @Override
    public Set<Director> getAllDirectors() {
        String sql = "select * from DIRECTORS";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToDirector));
    }

    @Override
    public Director addDirector(Director d) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("DIRECTOR_NAME", d.getName());
        KeyHolder keyHolder = jdbcInsert
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID")
                .executeAndReturnKeyHolder(values);
        d.setId(keyHolder.getKey().longValue());
        return d;
    }

    @Override
    public int updateDirector(Director d) {
        String sql = "update DIRECTORS " +
                "set DIRECTOR_NAME = ? " +
                "where DIRECTOR_ID = ?";
        return jdbcTemplate.update(sql, d.getName(), d.getId());
    }

    @Override
    public int deleteDirector(long id) {
        String sql = "delete from DIRECTORS where DIRECTOR_ID = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public Set<Director> getNames(Set<Director> directorsIds) {
        return directorsIds.stream().map(d -> getDirectorById(d.getId())).collect(Collectors.toSet());
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("DIRECTOR_ID"))
                .name(resultSet.getString("DIRECTOR_NAME"))
                .build();
    }

}
