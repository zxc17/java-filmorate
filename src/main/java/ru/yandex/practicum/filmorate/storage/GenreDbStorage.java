package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre get(long id) {
        String sql = "select * from GENRES where GENRE_ID = ?";
        List<Genre> query = jdbcTemplate.query(sql, this::mapRowToGenre, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из БД GENRES, id=%s.", id));
        }
    }

    @Override
    public Set<Genre> getAll() {
        String sql = "select * from GENRES";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToGenre));
    }

    @Override
    public Set<Genre> getNames(Set<Genre> genresIds) {
        return genresIds.stream().map(g -> get(g.getId())).collect(Collectors.toSet());
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
