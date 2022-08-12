package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa get(long id) {
        String sql = "select * from MPA where MPA_ID = ?";
        List<Mpa> query = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        switch (query.size()) {
            case 0: return null;
            case 1: return query.get(0);
            default: throw new StorageException(String.format("Ошибка при запросе данных из БД MPA, id=%s.", id));
        }
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "select * from MPA";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int numRow) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }


}
