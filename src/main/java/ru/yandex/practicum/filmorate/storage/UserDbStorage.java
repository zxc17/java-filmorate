package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.customExceptions.StorageException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User u) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("LOGIN", u.getLogin());
        values.put("USER_NAME", u.getName());
        values.put("EMAIL", u.getEmail());
        values.put("BIRTHDAY", u.getBirthday());
        KeyHolder keyHolder = jdbcInsert
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID")
                .executeAndReturnKeyHolder(values);
        u.setId(keyHolder.getKey().longValue());
        return u;
    }

    @Override
    public User get(long id) {
        String sql = "select * from USERS where USER_ID = ?";
        List<User> query = jdbcTemplate.query(sql, this::mapRowToUser, id);
        switch (query.size()) {
            case 0:
                return null;
            case 1:
                return query.get(0);
            default:
                throw new StorageException(String.format("Ошибка при запросе данных из БД USERS, id=%s.", id));
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User update(User u) {
        String sql = "update USERS " +
                "set LOGIN = ?, USER_NAME = ?, EMAIL = ?, BIRTHDAY = ?" +
                "where USER_ID = ?";
        if (jdbcTemplate.update(sql, u.getLogin(), u.getName(), u.getEmail(), u.getBirthday(), u.getId()) == 0)
            throw new StorageException(String.format("Ошибка при обновлении данных в БД USERS, id=%s.", u.getId()));
        return u;
    }

    @Override
    public void remove(long id) {
        String sql = "delete from USERS where USER_ID = ?";
        if (jdbcTemplate.update(sql, id) == 0)
            throw new StorageException(String.format("Ошибка при удалении из БД USERS, id=%s.", id));
    }

    @Override
    public void clear() {
        String sql = "delete from USERS";
        jdbcTemplate.update(sql);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
    }
}
